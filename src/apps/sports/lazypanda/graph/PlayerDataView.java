package apps.sports.lazypanda.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import apps.sports.lazypanda.R;

public class PlayerDataView extends View {

	TextView playerName;
	private float graphWidth;
	private float graphHeight;
	private final Rect graphRect = new Rect();

	/**
	 * Constructor. Passes in player data so we can draw the graph
	 * 
	 * @param context
	 */
	public PlayerDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		graphWidth = w;
		graphHeight = h / 2f;
		Log.d("LazyPanda", "OnSizeChanged was called");
		getGraphRect(graphRect);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		this.setMeasuredDimension(parentWidth, parentHeight / 2);
	}

	/**
	 * Create the rectangle that defines the ownership graph of players
	 * 
	 * @param x
	 * @param y
	 * @param rect
	 */
	private void getGraphRect(Rect rect) {
		rect.set(0, 0, (int) graphWidth, (int) graphHeight);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the background (technically same color as default Android
		// backgroun)
		Paint background = new Paint();
		background.setColor(getResources().getColor(
				android.R.color.background_dark));
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);

		// Draw outer lines (box)
		Paint outerLines = new Paint();
		Paint outerLinesHiLite = new Paint();
		outerLines.setColor(getResources()
				.getColor(android.R.color.darker_gray));
		outerLinesHiLite.setColor(getResources()
				.getColor(android.R.color.white));
		canvas.drawLine(0, 0, 0, getHeight(), outerLines);
		canvas.drawLine(1, 0, 1, getHeight(), outerLinesHiLite);
		canvas.drawLine((getWidth() - 2), 0, (getWidth() - 2), getHeight(),
				outerLines);
		canvas.drawLine((getWidth() - 1), 0, (getWidth() - 1), getHeight(),
				outerLinesHiLite);
		canvas.drawLine(0, 0, getWidth(), 0, outerLines);
		canvas.drawLine(0, 1, getWidth(), 1, outerLinesHiLite);
		canvas.drawLine(0, (getHeight() - 2), getWidth(), (getHeight() - 2),
				outerLines);
		canvas.drawLine(0, (getHeight() - 1), getWidth(), (getHeight() - 1),
				outerLinesHiLite);

		// Draw grid lines
		Paint horGridLines = new Paint();
		Paint verGridLines = new Paint();
		horGridLines.setColor(getResources().getColor(R.color.gray25));
		verGridLines.setColor(getResources().getColor(R.color.gray25));
		// draw horizontals
		for (int i = 0; i < 5; i++) {
			canvas.drawLine(0, getHeight() * i / 5, getWidth(), getHeight() * i
					/ 5, horGridLines);
		}
		// draw verticals
		for (int i = 0; i < 8; i++) {
			canvas.drawLine(getWidth() * i / 8, 0, getWidth() * i / 8,
					getHeight(), verGridLines);
		}
	}
}
