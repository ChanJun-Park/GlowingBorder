package com.jingom.android.glowing.border

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

fun Modifier.glowingBorder(
	color: Color,
	strokeWidth: Float,
	glowingLength: Float,
	glowingSpeed: Float
): Modifier = composed {
	val infiniteTransition = rememberInfiniteTransition(label = "glowing border transition")
	val animatedFloat by infiniteTransition.animateFloat(
		initialValue = 0f,
		targetValue = 1f,
		animationSpec = infiniteRepeatable(
			animation = tween(durationMillis = glowingSpeed.toInt(), easing = LinearEasing),
			repeatMode = RepeatMode.Restart
		),
		label = "glowing border animation"
	)
	val pathMeasure = remember { PathMeasure() }
	val path = remember { Path() }
	val glowingPath = remember { Path() }

	this.drawBehind {
		// Get the size of the UI element
		val borderSize = size
		val halfStroke = strokeWidth / 2

		// Draw a base border
		drawRoundRect(
			color = color.copy(alpha = 0.2f),
			size = borderSize,
			style = Stroke(width = strokeWidth),
			cornerRadius = CornerRadius(12f, 12f)
		)

		// Draw a glowing part of the border
		path.reset()
		path.addRoundRect(
			RoundRect(
				rect = Rect(
					left = halfStroke,
					top = halfStroke,
					right = borderSize.width - halfStroke,
					bottom = borderSize.height - halfStroke
				),
				cornerRadius = CornerRadius(12f, 12f)
			)
		)

		pathMeasure.setPath(path, false)

		val glowingStart = animatedFloat * pathMeasure.length // Start position of glowing part
		val glowingEnd = glowingStart + glowingLength // End position of glowing part

		val headOffset = pathMeasure.getPosition(glowingEnd)

		// 새로운 Path에 경로의 일부 구간을 추가
		glowingPath.apply {
			reset()
			pathMeasure.getSegment(glowingStart, glowingEnd, this, true)
		}

		drawPath(
			path = glowingPath,
			color = color,
			style = Stroke(width = strokeWidth),
			alpha = 1f
		)

		drawCircle(
			color = color,
			radius = strokeWidth,
			center = Offset(headOffset.x, headOffset.y)
		)
	}
}

@Composable
private fun GlowingBorderBox(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier
			.size(200.dp)
			.glowingBorder(
				color = Color.Cyan,
				strokeWidth = 8f,
				glowingLength = 100f,
				glowingSpeed = 2000f
			)
			.padding(16.dp)
	) {
		BasicText(text = "Glowing Border")
	}
}

@Preview(showBackground = true)
@Composable
private fun GlowingBorderBoxPreview() {
	GlowingBorderBox()
}