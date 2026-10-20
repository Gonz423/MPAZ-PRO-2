package com.example.appmenu.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// set of material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* other default text styles to override
    titlelarge = textstyle
        fontfamily = fontfamily.default,
        fontweight = fontweight.normal,
        fontsize = 22.sp,
        lineheight = 28.sp,
        letterspacing = 0.sp
    ,
    labelsmall = textstyle
        fontfamily = fontfamily.default,
        fontweight = fontweight.medium,
        fontsize = 11.sp,
        lineheight = 16.sp,
        letterspacing = 0.5.sp
    
    */
)