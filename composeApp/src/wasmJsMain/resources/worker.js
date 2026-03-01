// composeApp/src/wasmJsMain/resources/worker.js

// 1. 引入本地目录下的 sql-wasm.js
importScripts("./sql-wasm.js");

let db = null;
let initPromise = null;

async function initDb(dbUrl) {
    // 2. 初始化引擎，指定从本地同级目录加载 sql-wasm.wasm
    const SQL = await initSqlJs({
        locateFile: file => `./${file}`
    });

    try {
        // 3. 抓取 commonMain/composeResources 传来的真实 DB 路径
        const response = await fetch(dbUrl);
        if (!response.ok) throw new Error("HTTP status " + response.status);
        const buffer = await response.arrayBuffer();

        // 4. 使用你的 2MB 二进制数据装载数据库
        db = new SQL.Database(new Uint8Array(buffer));
        console.log("本地数据库加载成功! DB大小:", buffer.byteLength, "bytes");
    } catch (e) {
        console.error("加载预置数据库失败，创建空白库: ", e);
        db = new SQL.Database();
    }
}

// 5. 对接 SQLDelight 和 初始化指令
self.onmessage = async function(event) {
    const data = event.data;

    if (data.action === "init_custom_db") {
        initPromise = initDb(data.url);
        await initPromise;
        self.postMessage({ id: data.id, action: "init_complete" });
        return;
    }

    if (!initPromise) return;
    await initPromise;

    try {
        switch (data.action) {
            case "exec":
                let results = { values:[] }; // 默认空结果，符合 SQLDelight 的要求

                // 1. 如果有带参数 (例如你的带条件查询 nameZhCN = ?)
                if (data.params && data.params.length > 0) {
                    const stmt = db.prepare(data.sql);
                    stmt.bind(data.params);

                    const rows =[];
                    while (stmt.step()) {
                        rows.push(stmt.get()); // 逐行读取数据
                    }

                    results = {
                        columns: stmt.getColumnNames(),
                        values: rows
                    };
                    stmt.free(); // 务必释放 Statement 内存
                }
                // 2. 如果没有参数 (例如 SELECT * 或不带参数的 INSERT)
                else {
                    const execRes = db.exec(data.sql);
                    if (execRes && execRes.length > 0) {
                        // sql.js 返回的是数组，SQLDelight 只要第一个对象
                        results = execRes[0];
                    }
                }

                // 返回给 Kotlin 端
                self.postMessage({ id: data.id, results: results });
                break;

            case "begin":
                db.exec("BEGIN TRANSACTION;");
                self.postMessage({ id: data.id });
                break;
            case "commit":
                db.exec("COMMIT;");
                self.postMessage({ id: data.id });
                break;
            case "rollback":
                db.exec("ROLLBACK;");
                self.postMessage({ id: data.id });
                break;
            case "close":
                db.close();
                self.postMessage({ id: data.id });
                break;
        }
    } catch (error) {
        self.postMessage({ id: data.id, error: error.message });
    }
};