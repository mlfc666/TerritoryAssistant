package moe.mlfc.territory.assistant.ui.theme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import moe.mlfc.territory.assistant.theme.*
import org.jetbrains.compose.resources.Font
import territory_assistant.composeapp.generated.resources.Res
import territory_assistant.composeapp.generated.resources.SourceHanSansSCRegular

val MonochromeLightColorScheme = lightColorScheme(
    // 核心：主要操作和选中态使用黑色
    primary = DarkGrey,
    onPrimary = White,
    primaryContainer = White,
    // 背景：使用极浅的灰色区分页面与卡片
    background = OffWhite,
    onBackground = DarkGrey,

    // 容器：卡片、弹窗使用纯白
    surface = White,
    onSurface = DarkGrey,

    // 变体文字：用于 ID 100001 这种次要信息
    onSurfaceVariant = MediumGrey,

    // 边框：用于截图中的细线边框
    outline = LightGrey,
    outlineVariant = LightGrey,

    // 错误色：仅在失败/警告时出现
    error = ErrorRed,
    onError = White
)
@Composable
fun getWikiTypography(): androidx.compose.material3.Typography {
    // 1. 定义字体族
    val wikiFontFamily = FontFamily(
        Font(Res.font.SourceHanSansSCRegular, FontWeight.Normal)
    )

    // 2. 创建并返回自定义 Typography
    // 我们必须手动为每种样式指定 fontFamily，否则它会退回到系统默认字体
    return androidx.compose.material3.Typography(
        displayLarge = TextStyle(fontFamily = wikiFontFamily),
        displayMedium = TextStyle(fontFamily = wikiFontFamily),
        displaySmall = TextStyle(fontFamily = wikiFontFamily),
        headlineLarge = TextStyle(fontFamily = wikiFontFamily),
        headlineMedium = TextStyle(fontFamily = wikiFontFamily),
        headlineSmall = TextStyle(fontFamily = wikiFontFamily),
        titleLarge = TextStyle(fontFamily = wikiFontFamily),
        titleMedium = TextStyle(fontFamily = wikiFontFamily),
        titleSmall = TextStyle(fontFamily = wikiFontFamily),
        bodyLarge = TextStyle(fontFamily = wikiFontFamily),
        bodyMedium = TextStyle(fontFamily = wikiFontFamily),
        bodySmall = TextStyle(fontFamily = wikiFontFamily),
        labelLarge = TextStyle(fontFamily = wikiFontFamily),
        labelMedium = TextStyle(fontFamily = wikiFontFamily),
        labelSmall = TextStyle(fontFamily = wikiFontFamily)
    )
}
@Composable
fun TerritoryAssistantTheme(
    content: @Composable () -> Unit
) {
    // 强制使用浅色模式以符合截图风格
    MaterialTheme(
        colorScheme = MonochromeLightColorScheme,
        typography = getWikiTypography(),
        // 这里的 Typography 可以设置更简洁的无衬线字体
        content = content
    )
}