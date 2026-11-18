import UIKit
import shared

/// Hosts the Compose content and renders the navigation elements (tab bar, navigation bar title,
/// back and close buttons) above it using system components, so they get the Liquid Glass
/// appearance and are placed like in native apps.
///
/// The tab bar belongs to a system `UITabBarController` with a `UINavigationController` in each
/// tab. UIKit places only the bars of its containers, e.g. down the side on iPhone Duo, never a
/// standalone `UITabBar` or `UINavigationBar`. The tabs hold empty placeholders, the whole UI stays
/// in one Compose view below them, as moving it between tabs would take it out of the window and
/// dispose its scene. Touches that don't hit the bars or the buttons pass through to the Compose
/// view.
@available(iOS 26.0, *)
final class LiquidGlassNavigationViewController: UIViewController, IosNativeNavigationRenderer,
    UITabBarControllerDelegate
{

    private static let buttonSize: CGFloat = 44

    private let tabsController = UITabBarController()
    private var content: UIViewController?
    private var tabs: [IosNativeTab] = []
    private var buttons: [String: UIButton] = [:]
    private var buttonActions: [String: () -> Void] = [:]
    private var navigationBarButtons: [String: NavigationBarButton] = [:]
    private var navigationBarTitles: [String: NavigationBarTitle] = [:]
    private var navigationBarSequence = 0
    private var appliedNavigationBar: AppliedNavigationBar?
    private var isNavigationBarUpdateScheduled = false
    private var onTabSelected: ((KotlinInt) -> Void)?
    private var onBarsInsetsChanged: ((IosNativeBarsInsets) -> Void)?
    /// Where the bars are, kept while they are hidden, so the content doesn't change size.
    private var barsInsets = BarsInsets()
    private var reportedBarsInsets: BarsInsets?

    override var childForStatusBarStyle: UIViewController? { content }

    override var childForHomeIndicatorAutoHidden: UIViewController? { content }

    override func loadView() {
        let view = PassthroughView()
        view.passesThrough = { [weak self] hit in self?.passesThrough(hit) ?? false }
        view.target = { [weak self] in self?.content?.view }
        self.view = view
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        tabsController.delegate = self
        tabsController.view.backgroundColor = .clear
        tabsController.setTabBarHidden(true, animated: false)
        if UIDevice.current.userInterfaceIdiom == .pad {
            // iPadOS shows the tab bar of a regular width at the top, over the Compose top bars.
            tabsController.traitOverrides.horizontalSizeClass = .compact
        }
        addChild(tabsController)
        tabsController.view.frame = view.bounds
        tabsController.view.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        view.addSubview(tabsController.view)
        tabsController.didMove(toParent: self)
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        updateBarsInsets()
    }

    /// Adds the Compose content below the native navigation elements.
    func embed(_ content: UIViewController) {
        loadViewIfNeeded()
        self.content = content
        addChild(content)
        content.view.translatesAutoresizingMaskIntoConstraints = false
        view.insertSubview(content.view, at: 0)
        NSLayoutConstraint.activate([
            content.view.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            content.view.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            content.view.topAnchor.constraint(equalTo: view.topAnchor),
            content.view.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
        content.didMove(toParent: self)
        setNeedsStatusBarAppearanceUpdate()
    }

    // MARK: - IosNativeNavigationRenderer

    func setDarkTheme(isDarkTheme: Bool) {
        overrideUserInterfaceStyle = isDarkTheme ? .dark : .light
    }

    func setTabs(tabs: [IosNativeTab], selectedIndex: Int32) {
        if tabs != self.tabs {
            self.tabs = tabs
            let viewControllers = tabs.enumerated().map { index, tab in
                let placeholder = UIViewController()
                placeholder.view = LayoutReportingView { [weak self] in self?.updateBarsInsets() }
                let navigationController = UINavigationController(rootViewController: placeholder)
                navigationController.navigationBar.tintColor = .label
                navigationController.tabBarItem = UITabBarItem(
                    title: tab.title,
                    image: UIImage(systemName: tab.systemImageName),
                    tag: index
                )
                return navigationController
            }
            tabsController.setViewControllers(viewControllers, animated: false)
            scheduleNavigationBarUpdate()
        }
        let index = Int(selectedIndex)
        if tabs.indices.contains(index) && tabsController.selectedIndex != index {
            tabsController.selectedIndex = index
            scheduleNavigationBarUpdate()
        }
        if tabsController.isTabBarHidden != tabs.isEmpty {
            tabsController.setTabBarHidden(tabs.isEmpty, animated: false)
        }
        updateBarsInsets()
    }

    func setOnTabSelectedListener(listener: @escaping (KotlinInt) -> Void) {
        onTabSelected = listener
    }

    func setOnBarsInsetsChangedListener(listener: @escaping (IosNativeBarsInsets) -> Void) {
        onBarsInsetsChanged = listener
        reportedBarsInsets = nil
        reportBarsInsets()
    }

    func setButton(
        id: String,
        systemImageName: String,
        accessibilityLabel: String,
        x: Double,
        y: Double,
        width: Double,
        height: Double,
        alpha: Double,
        onClick: @escaping () -> Void
    ) {
        removeNavigationBarButton(id: id)
        buttonActions[id] = onClick
        let button = buttons[id] ?? createButton(id: id)
        if button.configuration?.image == nil
            || button.accessibilityIdentifier != systemImageName
        {
            button.configuration?.image = UIImage(systemName: systemImageName)
            button.accessibilityIdentifier = systemImageName
        }
        button.accessibilityLabel = accessibilityLabel
        button.alpha = CGFloat(alpha)
        button.isUserInteractionEnabled = alpha > 0
        // Keep the system size of the button and center it in the space reserved by Compose
        let size = Self.buttonSize
        button.frame = CGRect(
            x: x + (width - size) / 2,
            y: y + (height - size) / 2,
            width: size,
            height: size
        )
    }

    func setNavigationBarButton(
        id: String,
        systemImageName: String,
        accessibilityLabel: String,
        isTop: Bool,
        onClick: @escaping () -> Void
    ) {
        buttons.removeValue(forKey: id)?.removeFromSuperview()
        buttonActions[id] = onClick
        let button: NavigationBarButton
        if let existing = navigationBarButtons[id] {
            button = existing
        } else {
            let item = UIBarButtonItem(
                primaryAction: UIAction(image: UIImage(systemName: systemImageName)) {
                    [weak self] _ in self?.buttonActions[id]?()
                }
            )
            button = NavigationBarButton(item: item, systemImageName: systemImageName)
            navigationBarButtons[id] = button
        }
        if button.systemImageName != systemImageName {
            button.systemImageName = systemImageName
            button.item.image = UIImage(systemName: systemImageName)
        }
        button.item.accessibilityLabel = accessibilityLabel
        if button.sequence == 0 || button.isTop != isTop {
            button.isTop = isTop
            navigationBarSequence += 1
            button.sequence = navigationBarSequence
            scheduleNavigationBarUpdate()
        }
    }

    func removeButton(id: String) {
        buttonActions[id] = nil
        buttons.removeValue(forKey: id)?.removeFromSuperview()
        removeNavigationBarButton(id: id)
    }

    func setNavigationBarTitle(id: String, title: String, isTop: Bool) {
        let registration = navigationBarTitles[id] ?? NavigationBarTitle()
        navigationBarTitles[id] = registration
        if registration.title != title {
            registration.title = title
            scheduleNavigationBarUpdate()
        }
        if registration.sequence == 0 || registration.isTop != isTop {
            registration.isTop = isTop
            navigationBarSequence += 1
            registration.sequence = navigationBarSequence
            scheduleNavigationBarUpdate()
        }
    }

    func removeNavigationBarTitle(id: String) {
        if navigationBarTitles.removeValue(forKey: id) != nil {
            scheduleNavigationBarUpdate()
        }
    }

    // MARK: - UITabBarControllerDelegate

    func tabBarController(
        _ tabBarController: UITabBarController,
        shouldSelect viewController: UIViewController
    ) -> Bool {
        // Compose switches to the tab right away, so it's selected without waiting for setTabs.
        onTabSelected?(KotlinInt(int: Int32(viewController.tabBarItem.tag)))
        return true
    }

    func tabBarController(
        _ tabBarController: UITabBarController,
        didSelect viewController: UIViewController
    ) {
        scheduleNavigationBarUpdate()
        updateBarsInsets()
    }

    // MARK: - Private

    private func createButton(id: String) -> UIButton {
        var configuration = UIButton.Configuration.glass()
        configuration.cornerStyle = .capsule
        let button = UIButton(
            configuration: configuration,
            primaryAction: UIAction { [weak self] _ in self?.buttonActions[id]?() }
        )
        button.tintColor = .label
        buttons[id] = button
        // Above the bars, so the button gets the touches
        view.addSubview(button)
        return button
    }

    private func removeNavigationBarButton(id: String) {
        if navigationBarButtons.removeValue(forKey: id) != nil {
            scheduleNavigationBarUpdate()
        }
    }

    /// Buttons and titles register while Compose applies a frame, e.g. the ones of the leaving and
    /// of the new top screen, they are applied together so the bar doesn't empty in between.
    private func scheduleNavigationBarUpdate() {
        guard !isNavigationBarUpdateScheduled else { return }
        isNavigationBarUpdateScheduled = true
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            isNavigationBarUpdateScheduled = false
            updateNavigationBar()
        }
    }

    private func updateNavigationBar() {
        guard
            let navigationController = tabsController.selectedViewController
                as? UINavigationController,
            let placeholder = navigationController.topViewController
        else { return }
        let item = navigationBarButtons.values.filter(\.isTop).max { $0.sequence < $1.sequence }?
            .item
        let title = navigationBarTitles.values.filter(\.isTop).max { $0.sequence < $1.sequence }?
            .title
        let applied = appliedNavigationBar
        if applied?.placeholder === placeholder && applied?.item === item
            && applied?.title == title
        {
            return
        }
        if let applied, applied.placeholder !== placeholder {
            applied.placeholder.navigationItem.title = nil
            applied.placeholder.navigationItem.setLeftBarButton(nil, animated: false)
        }
        let navigationItem = placeholder.navigationItem
        // The bar is never hidden, UIKit would slide it in from the top. It's invisible while
        // empty, so UIKit fades the item in and out where it is instead.
        if navigationItem.leftBarButtonItem !== item {
            navigationItem.setLeftBarButton(item, animated: applied != nil)
        }
        navigationItem.title = title
        appliedNavigationBar = AppliedNavigationBar(
            placeholder: placeholder,
            item: item,
            title: title
        )
    }

    /// The tab reports the tab bar in its safe area, or in its frame for a sidebar.
    private func updateBarsInsets() {
        guard isViewLoaded,
            let navigationController = tabsController.selectedViewController
                as? UINavigationController,
            navigationController.isViewLoaded,
            let tabView = navigationController.view,
            tabView.window != nil
        else { return }
        var insets = barsInsets
        if !tabsController.isTabBarHidden {
            let safeFrame = view.bounds.inset(by: view.safeAreaInsets)
            let tabSafeFrame = tabView.convert(
                tabView.bounds.inset(by: tabView.safeAreaInsets),
                to: view
            )
            insets.left = max(0, tabSafeFrame.minX - safeFrame.minX)
            insets.top = max(0, tabSafeFrame.minY - safeFrame.minY)
            insets.right = max(0, safeFrame.maxX - tabSafeFrame.maxX)
            insets.bottom = max(0, view.bounds.maxY - tabSafeFrame.maxY)
        }
        // The tab bar keeps its layout while hidden, it's the most reliable sign of vertical bars.
        let tabBarSize = tabsController.tabBar.frame.size
        insets.areBarsVertical = tabBarSize.height > tabBarSize.width
        insets.navigationBarBottom = navigationBarBottom(navigationController.navigationBar)
        barsInsets = insets
        reportBarsInsets()
    }

    /// Where the content below the navigation bar of the tab starts, a Compose top bar shown in the
    /// system bar takes this space. UIKit can lay the bar out next to the status bar, e.g. when it
    /// takes only a corner of the unfolded iPhone Duo. `nil` while the bar runs down a side.
    private func navigationBarBottom(_ navigationBar: UINavigationBar) -> CGFloat? {
        let frame = navigationBar.convert(navigationBar.bounds, to: view)
        guard frame.width > frame.height else { return nil }
        return frame.maxY
    }

    private func reportBarsInsets() {
        guard let onBarsInsetsChanged, reportedBarsInsets != barsInsets else { return }
        reportedBarsInsets = barsInsets
        onBarsInsetsChanged(
            IosNativeBarsInsets(
                left: Double(barsInsets.left),
                top: Double(barsInsets.top),
                right: Double(barsInsets.right),
                bottom: Double(barsInsets.bottom),
                areBarsVertical: barsInsets.areBarsVertical,
                navigationBarBottom: barsInsets.navigationBarBottom.map {
                    KotlinDouble(double: Double($0))
                }
            )
        )
    }

    private func passesThrough(_ hit: UIView) -> Bool {
        guard let tabBarView = tabsController.viewIfLoaded else { return false }
        if hit === tabBarView { return true }
        let navigationController =
            tabsController.selectedViewController as? UINavigationController
        // Touches between the items of the navigation bar go to the Compose content under it.
        if let navigationBar = navigationController?.navigationBar,
            hit.isDescendant(of: navigationBar)
        {
            var view: UIView? = hit
            while let current = view, current !== navigationBar {
                if current is UIControl { return false }
                view = current.superview
            }
            return true
        }
        // The empty placeholder and the containers around it.
        var container = navigationController?.topViewController?.viewIfLoaded
        while let current = container, current !== tabBarView {
            if current === hit { return true }
            container = current.superview
        }
        return false
    }
}

private struct BarsInsets: Equatable {
    var left: CGFloat = 0
    var top: CGFloat = 0
    var right: CGFloat = 0
    var bottom: CGFloat = 0
    var areBarsVertical = false
    var navigationBarBottom: CGFloat?
}

private final class NavigationBarButton {
    let item: UIBarButtonItem
    var systemImageName: String
    var isTop = false
    /// Order in which the buttons became the top one or left, the latest top one is shown.
    var sequence = 0

    init(item: UIBarButtonItem, systemImageName: String) {
        self.item = item
        self.systemImageName = systemImageName
    }
}

private final class NavigationBarTitle {
    var title = ""
    var isTop = false
    /// Shares the order of [NavigationBarButton.sequence].
    var sequence = 0
}

private struct AppliedNavigationBar {
    let placeholder: UIViewController
    let item: UIBarButtonItem?
    let title: String?
}

/// Hands the touches that hit [passesThrough] views over to the view of [target].
private final class PassthroughView: UIView {
    var passesThrough: (UIView) -> Bool = { _ in false }
    var target: () -> UIView? = { nil }

    override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
        let hit = super.hitTest(point, with: event)
        guard let hit, passesThrough(hit), let target = target() else { return hit }
        return target.hitTest(convert(point, to: target), with: event)
    }
}

/// Placeholder of a tab, which reports when the bars around it move.
private final class LayoutReportingView: UIView {
    private let onLayout: () -> Void

    init(onLayout: @escaping () -> Void) {
        self.onLayout = onLayout
        super.init(frame: .zero)
    }

    @available(*, unavailable)
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    override func layoutSubviews() {
        super.layoutSubviews()
        onLayout()
    }

    override func safeAreaInsetsDidChange() {
        super.safeAreaInsetsDidChange()
        onLayout()
    }
}
