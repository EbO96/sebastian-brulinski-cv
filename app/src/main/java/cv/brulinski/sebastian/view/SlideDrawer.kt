package cv.brulinski.sebastian.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.ScaleAnimation
import android.widget.*
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cv.brulinski.sebastian.R

class SlideDrawer(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /*
    Interfaces
     */
    interface MenuItemsClickListener {
        fun onClick(position: Int, drawerMenuItem: DrawerMenuItem)
    }

    private interface HolderBinder {
        fun bind(position: Int)
    }

    /*
    Constants
     */
    //Drawer view const
    private val drawerRootViewId = "1"
    private val closeButtonViewId = "2"
    private val recyclerViewId = "3"
    private val itemIconViewId = "4"
    private val itemTitleViewId = "5"
    private val itemSubtitleViewId = "6"
    private val itemViewId = "7"
    private val drawerTitleTextViewId = "8"

    /*
    XML attributes
    */
    private val attr = context?.obtainStyledAttributes(attrs, R.styleable.SlideDrawer)

    /*
    Config
  */
    private val cfg: Config

    /*
    Theme
     */
    private val theme = Theme()

    /*
    ViewCreator
   */
    private val viewCreator = ViewCreator()
    private val mDrawerView = viewCreator.createDrawerView()

    /*
    Menu
     */
    private var menuItemsClickListener: MenuItemsClickListener? = null //Menu callback
    private var menuAdapter: MenuAdapter? = null //Adapter

    /*
    Views
     */
    private var contentWrapper = FrameLayout(context)
    private var contentView: View? = attr?.getResourceId(R.styleable.SlideDrawer_mainContent, -1).inflateLayout() //User content layout
    private var drawerView: View? = mDrawerView.view
    private var menuRecycler: RecyclerView? = mDrawerView.findView(recyclerViewId) //Drawer recycler to display menu items
    private var drawerTitleTextView: TextView? = mDrawerView.findView(drawerTitleTextViewId)
    private var closeIcon: ImageView? = mDrawerView.findView(closeButtonViewId)
    private var drawerRoot: LinearLayout? = mDrawerView.findView(drawerRootViewId)

    /*
    Animation
     */
    private var animSetOpen = AnimatorSet()
    private var animSetClose = AnimatorSet()
    private var openAnimationX: ObjectAnimator? = null
    private var openAnimationY: ObjectAnimator? = null
    private var closeAnimationX: ObjectAnimator? = null
    private var closeAnimationY: ObjectAnimator? = null
    private var closeButtonScaleShowAnimator: ScaleAnimation? = null
    private var closeButtonScaleHideAnimator: ScaleAnimation? = null
    private var showMenuAnimation: ObjectAnimator? = null
    private var hideMenuAnimation: ObjectAnimator? = null

    init {

        if (drawerView == null) drawerView = ConstraintLayout(context)

        drawerView?.apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        addView(drawerView)

        if (contentView == null) contentView = ConstraintLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        }

        contentWrapper.apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            isClickable = true
            isFocusable = true
            elevation = 16f
        }

        contentWrapper.addView(contentView)
        addView(contentWrapper)

        cfg = Config(context)
        contentWrapper.setBackgroundColor(theme.drawerBackgroundColor)

        closeButtonScaleShowAnimator = ScaleAnimation(cfg.closeButtonScaleFrom,
                cfg.closeButtonScaleTo,
                cfg.closeButtonScaleFrom,
                cfg.closeButtonScaleTo,
                Animation.RELATIVE_TO_SELF,
                cfg.closeButtonPivotX,
                Animation.RELATIVE_TO_SELF,
                cfg.closeButtonPivotY).apply {
            fillAfter = true
            duration = cfg.duration
        }

        closeButtonScaleHideAnimator = ScaleAnimation(cfg.closeButtonScaleTo,
                cfg.closeButtonScaleFrom,
                cfg.closeButtonScaleTo,
                cfg.closeButtonScaleFrom,
                Animation.RELATIVE_TO_SELF,
                cfg.closeButtonPivotX,
                Animation.RELATIVE_TO_SELF,
                cfg.closeButtonPivotY).apply {
            fillAfter = true
            duration = cfg.duration
        }

        showMenuAnimation = ObjectAnimator.ofFloat(menuRecycler, "alpha", 0f, 1f).applyDefaultConfig()
        hideMenuAnimation = ObjectAnimator.ofFloat(menuRecycler, "alpha", 1f, 0f).applyDefaultConfig()

        hideCloseButton()
        theme.drawerTitle = "Menu"

        closeIcon?.setOnClickListener {
            close()
        }
    }

    private fun ObjectAnimator.applyDefaultConfig(animator: ((ObjectAnimator) -> Unit?)? = null): ObjectAnimator {
        interpolator = LinearInterpolator()
        duration = cfg.duration
        animator?.let { it(this) }
        return this
    }

    /*
    Public methods
     */

    operator fun invoke(body: Theme.() -> Unit, slideDrawer: SlideDrawer.() -> Unit) {
        theme.let { body(it) }
        slideDrawer(this)
    }

    fun isOpen() = cfg.isOpen

    fun open() {
        if (!cfg.isOpen) {
            contentView?.apply {
                if (openAnimationX == null || openAnimationY == null) {
                    if (openAnimationX == null)
                        openAnimationX = ObjectAnimator.ofFloat(contentWrapper, "translationX", 0f, cfg.screenWidth)
                    if (openAnimationY == null)
                        openAnimationY = ObjectAnimator.ofFloat(contentWrapper, "translationY", 0f, cfg.screenHeight)
                }
                animSetOpen.apply {
                    end()
                    interpolator = LinearInterpolator()
                    duration = cfg.duration
                    playTogether(openAnimationX, openAnimationY)
                    start()
                    showCloseButton()
                    showMenuList()
                }

                cfg.isOpen = true
            }
        }
    }

    fun close(end: (() -> Unit)? = null) {
        if (cfg.isOpen) {
            contentView?.apply {
                if (closeAnimationX == null || closeAnimationY == null) {
                    if (closeAnimationX == null)
                        closeAnimationX = ObjectAnimator.ofFloat(contentWrapper, "translationX", cfg.screenWidth, 0f)
                    if (closeAnimationY == null)
                        closeAnimationY = ObjectAnimator.ofFloat(contentWrapper, "translationY", cfg.screenHeight, 0f)
                }
                animSetClose.apply {
                    end()
                    interpolator = LinearInterpolator()
                    duration = cfg.duration
                    playTogether(closeAnimationX, closeAnimationY)
                    start()
                    hideCloseButton()
                    hideMenuList()
                }
                animSetClose.endAnimationListener {
                    end?.let { it1 -> it1() }
                }
                cfg.isOpen = false
            }
        }
    }


    fun getContentView() = contentView


    fun setMenu(arrayList: ArrayList<DrawerMenuItem>, globalMenuTheme: GlobalMenuTheme? = null) {
        setMenuItems(arrayList, globalMenuTheme)
    }

    fun setMenu(vararg drawerMenuItem: DrawerMenuItem, globalMenuTheme: GlobalMenuTheme? = null) {
        setMenuItems(ArrayList(drawerMenuItem.asList()), globalMenuTheme)
    }

    fun setMenuItemClickListener(menuItemsClickListener: MenuItemsClickListener?) {
        this.menuItemsClickListener = menuItemsClickListener
    }

    fun setDrawerTitle(title: String) {
        theme.drawerTitle = title
    }

    fun updateMenu(position: Int) {
        menuAdapter?.items?.withIndex()?.forEach {
            it.value.selected = it.index == position
        }
        menuAdapter?.notifyDataSetChanged()
    }

    /*
    Private methods
    */

    private fun showCloseButton() {
        closeButtonScaleShowAnimator?.let {
            closeIcon?.startAnimation(it)
        }
    }

    private fun hideCloseButton() {
        closeButtonScaleHideAnimator?.let {
            closeIcon?.startAnimation(it)
        }
    }

    private fun showMenuList() {
        if (theme.menuShowHideAnimation)
            showMenuAnimation?.apply {
                View.INVISIBLE
                endAnimationListener {
                    View.VISIBLE
                }
                start()
            }
    }

    private fun hideMenuList() {
        if (theme.menuShowHideAnimation)
            hideMenuAnimation?.apply {
                View.VISIBLE
                endAnimationListener {
                    View.INVISIBLE
                }
                start()
            }
    }

    private fun setMenuItems(items: ArrayList<DrawerMenuItem>, globalMenuTheme: GlobalMenuTheme?) {
        if (menuAdapter == null)
            menuAdapter = MenuAdapter(items, globalMenuTheme ?: GlobalMenuTheme())
        menuRecycler?.apply {
            val lm = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            theme.apply {
                if (itemsDividerEnabled) {
                    val divider = ItemDivider(lm.orientation, menuItemsDividerColor).getDivider()
                    addItemDecoration(divider)
                }
            }
            layoutManager = lm
            itemAnimator = DefaultItemAnimator()
            adapter = menuAdapter
        }
        menuAdapter?.notifyDataSetChanged()
    }

    private fun Int?.inflateLayout(): View? {
        this?.apply {
            if (this != -1)
                return LayoutInflater.from(context).inflate(this, this@SlideDrawer, false)
        }
        return null
    }

    private fun Int.parseColor() = ContextCompat.getColor(context, this)

    private fun Int.getDrawable() = ContextCompat.getDrawable(context, this)

    private fun ObjectAnimator.endAnimationListener(end: (Animator?) -> Unit): ObjectAnimator {
        this.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                end(p0)
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }

        })
        return this
    }

    private fun AnimatorSet.endAnimationListener(end: (Animator?) -> Unit) {
        this.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                end(p0)
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
    }

    private fun <T : View> CreatedView.findView(id: String) = this.view.findViewById<T>(ids[id]
            ?: -1)

    private fun Int.dp() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            toFloat(),
            context.resources.displayMetrics
    ).toInt()

    private fun Int.sp() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            toFloat(),
            context.resources.displayMetrics
    ).toInt()


    /*
    Menu list
     */
    private inner class MenuAdapter(val items: ArrayList<DrawerMenuItem>, private val globalMenuTheme: GlobalMenuTheme) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        init {
            items.firstOrNull()?.selected = theme.selectableMenuItems == true
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val menuItemHolder = MenuItemHolder(viewCreator.createMenuItem())
            return menuItemHolder
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as? HolderBinder)?.bind(position)
        }

        private inner class MenuItemHolder(createdView: CreatedView) : RecyclerView.ViewHolder(createdView.view), HolderBinder {

            private val menuItem = createdView.view

            private val itemIconImageView: ImageView? = menuItem.findViewById(createdView.ids[itemIconViewId]
                    ?: -1)
            private val titleTextView: TextView? = menuItem.findViewById(createdView.ids[itemTitleViewId]
                    ?: -1)
            private val subtitleTextView: TextView? = menuItem.findViewById(createdView.ids[itemSubtitleViewId]
                    ?: -1)
            private val root: ViewGroup? = menuItem.findViewById(createdView.ids[itemViewId]
                    ?: -1)

            override fun bind(position: Int) {
                val drawerMenuItem = items[position]
                val states = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf(-android.R.attr.state_selected))

                menuItem.apply {
                    drawerMenuItem.apply {


                        val titleTextColors = intArrayOf(globalMenuTheme.titleColorSelected.parseColor(),
                                globalMenuTheme.titleColor.parseColor())

                        val subtitleTextColors = intArrayOf(globalMenuTheme.subtitleColorSelected.parseColor(),
                                globalMenuTheme.subtitleColor.parseColor())

                        val titleColorStateList = ColorStateList(states, titleTextColors)
                        val subtitleColorStateList = ColorStateList(states, subtitleTextColors)

                        itemIconImageView?.apply {
                            if (iconDrawable != null) setImageDrawable(iconDrawable)
                            else {
                                iconDrawable?.let {
                                    setImageDrawable(it)
                                } ?: run {
                                    setImageDrawable(globalMenuTheme.menuItemIcon)
                                }
                            }
                        }
                        subtitleTextView?.apply {
                            visibility = if (subtitle.isNullOrBlank())
                                View.GONE
                            else {
                                text = subtitle
                                setTextColor(subtitleColorStateList)
                                View.VISIBLE
                            }
                        }

                        titleTextView?.apply {
                            text = title
                            setTextColor(titleColorStateList)

                            (layoutParams as? LinearLayout.LayoutParams)?.apply {
                                if (subtitleTextView?.visibility == View.GONE) {
                                    setPadding(8.dp(), 8.dp(), 0, 8.dp())
                                } else {
                                    setPadding(8.dp(), 0, 0, 0)
                                }
                            }
                        }

                        titleTextView?.isSelected = selected
                        subtitleTextView?.isSelected = selected

                        setOnClickListener {
                            if (theme.selectableMenuItems)
                                selectItem(position)
                            if (cfg.closeAfterItemClick)
                                close {
                                    menuItemsClickListener?.onClick(position, drawerMenuItem)
                                }
                            else menuItemsClickListener?.onClick(position, drawerMenuItem)
                        }
                    }
                }
            }

            private fun selectItem(position: Int) {
                items.withIndex().filter { it.value.selected }.map {
                    it.value.selected = false
                    notifyItemChanged(it.index)
                }
                items[position].selected = true
                notifyItemChanged(position)
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    data class DrawerMenuItem(var title: String? = null,
                              var subtitle: String? = null,
                              var iconDrawable: Drawable? = null,
                              var selected: Boolean = false)

    open inner class GlobalMenuTheme(var titleColor: Int = android.R.color.black,
                                     val subtitleColor: Int = android.R.color.black,
                                     var titleColorSelected: Int = android.R.color.holo_red_dark,
                                     val subtitleColorSelected: Int = android.R.color.holo_red_dark) {
        val menuItemIcon = viewCreator.createDefaultMenuItemIcon()
    }

    private inner class ItemDivider(private val orientation: Int, color: Int) {

        val colorDrawable = ColorDrawable(color)

        fun getDivider() = DividerItemDecoration(context, orientation).apply { setDrawable(colorDrawable) }
    }

    /*
    Config
     */
    private class Config(context: Context?) {
        var isOpen = false
        var duration = 333L
            set(value) {
                field = Math.abs(value)
            }
        var screenWidth = (context?.resources?.displayMetrics?.widthPixels ?: 0).toFloat() / 2
        var screenHeight = (context?.resources?.displayMetrics?.heightPixels
                ?: 0).toFloat() / 8
        var closeAfterItemClick = true
        var closeButtonScaleFrom = 0f
        var closeButtonScaleTo = 1f
        var closeButtonPivotX = 0.5f
        var closeButtonPivotY = 0.5f
    }

    /*
    Theme
     */
    inner class Theme {
        var drawerTitle = ""
            set(value) {
                field = value
                drawerTitleTextView?.text = value
            }
        var itemsDividerEnabled = true
        var menuItemIconEnabled = false
        var selectableMenuItems = true
        var menuShowHideAnimation = true
        var menuItemsDividerColor = Color.parseColor("#5578909C")
            set(value) {
                field = value.parseColor()
            }
        @ColorInt
        var defDrawerBackgroundColor = android.R.color.white.parseColor()
        @ColorInt
        var drawerBackgroundColor = defDrawerBackgroundColor
            set(value) {
                field = value.parseColor()
                drawerRoot?.setBackgroundColor(field)
            }

        @ColorInt
        private var defCloseIconColor = android.R.color.black.parseColor()
        @ColorInt
        var closeIconColor = defCloseIconColor
            set(value) {
                field = value
                closeIcon?.setColorFilter(closeIconColor.parseColor(), PorterDuff.Mode.SRC_IN)
            }

        var closeIconVisibility = View.VISIBLE
            set(value) {
                field = value
                closeIcon?.visibility = value
            }
    }

    /*
    Views factory
     */

    private inner class ViewCreator {

        private val defaultParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        private val weightOneLp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            weight = 1f
        }
        private val closeButtonDrawable = attr?.getDrawable(R.styleable.SlideDrawer_closeButtonDrawable)
                ?: ColorDrawable(
                        Color.parseColor("#FF000000")
                )

        fun createDrawerView(): CreatedView {

            var closeButtonId = -1
            var recyclerId = -1
            var drawerRootId = -1
            var drawerTitleId = -1

            val root = LinearLayout(context).apply {
                drawerRootId = View.generateViewId()
                id = drawerRootId
                orientation = LinearLayout.VERTICAL
                layoutParams = defaultParams
                setBackgroundColor(theme.drawerBackgroundColor
                        ?: android.R.color.white.parseColor())
            }

            val header = RelativeLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    weight = 5f
                }

                val closeButton = ImageView(context).apply {
                    closeButtonId = View.generateViewId()
                    id = closeButtonId
                    setImageDrawable(closeButtonDrawable) //TODO draw close button
                    layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        contentDescription = "Close menu drawer"
                        //setMargins(8.dp(), 8.dp(), 8.dp(), 8.dp())
                        setPadding(8.dp(), 8.dp(), 8.dp(), 8.dp())
                        addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                        addRule(RelativeLayout.ALIGN_PARENT_START)
                    }
                }

                addView(closeButton, 0)

                val titleTextView = TextView(context).apply {
                    drawerTitleId = ID()
                    id = drawerTitleId
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                    setTextColor(theme.closeIconColor)
                    layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        setPadding(8.dp(), 8.dp(), 8.dp(), 8.dp())
                        addRule(RelativeLayout.ALIGN_PARENT_TOP)
                        addRule(RelativeLayout.CENTER_HORIZONTAL)
                    }
                }

                addView(titleTextView, 1)
            }

            val content = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = weightOneLp

                val recyclerView = RecyclerView(context).apply {
                    recyclerId = View.generateViewId()
                    id = recyclerId
                    setBackgroundColor(android.R.color.transparent.parseColor())
                    layoutParams = weightOneLp
                }

                val view = View(context).apply {
                    layoutParams = weightOneLp
                }
                addView(recyclerView, 0)
                addView(view, 1)
            }

            root.addView(header, 0)
            root.addView(content, 1)

            val idsMap = mapOf(
                    recyclerViewId to recyclerId,
                    closeButtonViewId to closeButtonId,
                    drawerRootViewId to drawerRootId,
                    drawerTitleTextViewId to drawerTitleId
            )

            return CreatedView(root, idsMap)
        }

        fun createMenuItem(): CreatedView {

            var rootId = -1
            var iconId = -1
            var titleId = -1
            var subtitleId = -1

            val root = LinearLayout(context).apply {
                rootId = ID()
                id = rootId
                orientation = LinearLayout.HORIZONTAL
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    setPadding(8, 8, 8, 8)
                }
                val iconWrapper = FrameLayout(context).apply {
                    visibility = if (theme.menuItemIconEnabled)
                        View.VISIBLE
                    else View.GONE
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                        weight = 5f
                        marginEnd = 8
                    }
                    val itemIcon = ImageView(context).apply {
                        iconId = ID()
                        id = iconId
                        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            gravity = Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
                        }

                    }
                    addView(itemIcon)
                }
                val titleSubtitleWrapper = LinearLayout(context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                        weight = 1f
                    }

                    val titleTextView = TextView(context).apply {
                        titleId = ID()
                        id = titleId
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            weight = 1f
                            setPadding(8.dp(), 0, 0, 0)
                        }
                    }
                    val subtitleTextView = TextView(context).apply {
                        subtitleId = ID()
                        id = subtitleId
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                        layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            setPadding(8.dp(), 0, 0, 0)
                            weight = 1f
                            topMargin = 8
                        }
                    }
                    addView(titleTextView, 0)
                    addView(subtitleTextView, 1)
                }
                addView(iconWrapper, 0)
                addView(titleSubtitleWrapper, 1)
            }

            val idsMap = mapOf(itemViewId to rootId,
                    itemIconViewId to iconId,
                    itemTitleViewId to titleId,
                    itemSubtitleViewId to subtitleId)

            return CreatedView(root, idsMap)
        }

        fun createDefaultMenuItemIcon() = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor("#FF000000"))
            setSize(16.sp(), 16.sp())
        }

        fun createBackgroundDrawable(color: ColorStateList) = GradientDrawable().apply {
            setColor(color)
        }

        private fun ID() = View.generateViewId()

    }

    private data class CreatedView(val view: View,
                                   val ids: Map<String, Int>)

}