package com.github.pgreze.reactions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow

/**
 * Entry point for reaction popup.
 */
class ReactionPopup @JvmOverloads constructor(
        context: Context,
        reactionsConfig: ReactionsConfig,
        var reactionSelectedListener: ReactionSelectedListener? = null
) : PopupWindow(context), View.OnTouchListener {

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
    }
    private val view: ReactionViewGroup by lazy(LazyThreadSafetyMode.NONE) {
        // Lazily inflate content during first display
        ReactionViewGroup(context, reactionsConfig).also {
            it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER)

            it.reactionSelectedListener = reactionSelectedListener

            rootView.addView(it)
        }.also { it.dismissListener = ::dismiss }
    }

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    var tempView: View? = null

    val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent?) {
            if (e != null) {
                showAtLocation(tempView, Gravity.NO_GRAVITY, 0, 0)
                view.show(e, tempView!!)
            }
            super.onLongPress(e)
        }
    });

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (!isShowing) {
            tempView = v
            gestureDetector.onTouchEvent(event)
            // Show fullscreen with button as context provider

        }
        return view.onTouchEvent(event)
    }

    override fun dismiss() {
        view.dismiss()
        super.dismiss()
    }
}
