import UIKit
import shared

/// Hosts the Compose content and renders the navigation elements (tab bar, back and close
/// buttons) above it using system components, so they get the Liquid Glass appearance.
@available(iOS 26.0, *)
final class LiquidGlassNavigationViewController: UIViewController, IosNativeNavigationRenderer,
    UITabBarDelegate
{

    private static let buttonSize: CGFloat = 44

    private let tabBar = UITabBar()
    private var buttons: [String: UIButton] = [:]
    private var buttonActions: [String: () -> Void] = [:]
    private var onTabSelected: ((KotlinInt) -> Void)?
    private var onTabBarHeightChanged: ((KotlinDouble) -> Void)?
    private var reportedTabBarHeight: CGFloat = -1

    override var childForStatusBarStyle: UIViewController? { children.first }

    override var childForHomeIndicatorAutoHidden: UIViewController? { children.first }

    override func viewDidLoad() {
        super.viewDidLoad()
        tabBar.delegate = self
        tabBar.isHidden = true
        tabBar.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(tabBar)
        NSLayoutConstraint.activate([
            tabBar.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            tabBar.trailingAnchor.constraint(equalTo: view.trailingAnchor),
            tabBar.bottomAnchor.constraint(equalTo: view.bottomAnchor),
        ])
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        reportTabBarHeight()
    }

    /// Adds the Compose content below the native navigation elements.
    func embed(_ content: UIViewController) {
        loadViewIfNeeded()
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
    }

    // MARK: - IosNativeNavigationRenderer

    func setDarkTheme(isDarkTheme: Bool) {
        overrideUserInterfaceStyle = isDarkTheme ? .dark : .light
    }

    func setTabs(tabs: [IosNativeTab], selectedIndex: Int32) {
        let currentTitles = tabBar.items?.map { $0.title ?? "" } ?? []
        if currentTitles != tabs.map({ $0.title }) {
            let items = tabs.enumerated().map { index, tab in
                UITabBarItem(
                    title: tab.title,
                    image: UIImage(systemName: tab.systemImageName),
                    tag: index
                )
            }
            tabBar.setItems(items, animated: false)
        }
        tabBar.isHidden = tabs.isEmpty
        let selectedItem = tabBar.items?.first { $0.tag == Int(selectedIndex) }
        if tabBar.selectedItem !== selectedItem {
            tabBar.selectedItem = selectedItem
        }
        view.setNeedsLayout()
    }

    func setOnTabSelectedListener(listener: @escaping (KotlinInt) -> Void) {
        onTabSelected = listener
    }

    func setOnTabBarHeightChangedListener(listener: @escaping (KotlinDouble) -> Void) {
        onTabBarHeightChanged = listener
        reportedTabBarHeight = -1
        reportTabBarHeight()
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

    func removeButton(id: String) {
        buttonActions[id] = nil
        buttons.removeValue(forKey: id)?.removeFromSuperview()
    }

    // MARK: - UITabBarDelegate

    func tabBar(_ tabBar: UITabBar, didSelect item: UITabBarItem) {
        onTabSelected?(KotlinInt(int: Int32(item.tag)))
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
        view.addSubview(button)
        return button
    }

    private func reportTabBarHeight() {
        let height = tabBar.isHidden ? 0 : max(0, view.bounds.maxY - tabBar.frame.minY)
        if height != reportedTabBarHeight {
            reportedTabBarHeight = height
            onTabBarHeightChanged?(KotlinDouble(double: Double(height)))
        }
    }
}
