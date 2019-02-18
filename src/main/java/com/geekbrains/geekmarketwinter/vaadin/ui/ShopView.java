package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.services.ProductService;
import com.geekbrains.geekmarketwinter.services.ShoppingCartService;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.github.appreciated.card.Card;
import com.github.appreciated.card.action.Actions;
import com.github.appreciated.card.content.HorizontalCardComponentContainer;
import com.github.appreciated.card.content.IconItem;
import com.github.appreciated.card.content.Item;
import com.github.appreciated.card.label.PrimaryLabel;
import com.github.appreciated.card.label.SecondaryLabel;
import com.github.appreciated.card.label.TitleLabel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.NumberFormat;
import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.LOCALE_RU;
import static com.geekbrains.geekmarketwinter.config.support.Constants.SHOP_PAGE;

@PageTitle("Shop")
@Route(SHOP_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class ShopView extends VerticalLayout {

    private final AuthRepository auth;
    private final ProductService productService;
    private final ShoppingCartService cartService;
    private ProductFilter productFilter;
    private Page<Product> page;
    private final CategoryService categoryService;
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance(LOCALE_RU);

    public ShopView(AuthRepository auth,
                    ProductService productService,
                    ShoppingCartService cartService,
                    CategoryService categoryService) {
        this.cartService = cartService;
        this.productService = productService;
        this.productFilter = new ProductFilter();
        this.categoryService = categoryService;
        this.page = productService.findAll(productFilter, Pageable.unpaged());
        this.auth = auth;
        init();
    }

    private void init() {
        HorizontalCardComponentContainer cardContainer = new HorizontalCardComponentContainer();
        cardContainer.setJustifyContentMode(JustifyContentMode.CENTER);
        cardContainer.getStyle().set("flex-wrap", "wrap");
        cardContainer.getStyle().set("display", "flex");
        cardContainer.setSpacing(false);
        cardContainer.setPadding(true);
        List<Product> products = getAllProducts();
        products.forEach(product -> {
            Card card = createCard(product);
            cardContainer.add(card);
        });
        CustomAppLayout appLayout = new CustomAppLayout(auth, cardContainer);
        add(appLayout);
        setAlignItems(Alignment.STRETCH);
    }

    private Card createCard(Product product) {
        Image defaultImage = createImage("images/users-png.png", "Coming soon");
        Image productImage = null;
        if (product.getImages().size() > 0) {
            productImage = createImage("images/" + product.getImages().get(0).getPath(), product.getTitle());
//            productImage.setHeight("150px");
//            productImage.setWidth("150px");
        }

        String price = numberFormat.format(product.getPrice());
        SecondaryLabel priceLabel = new SecondaryLabel(price);
        priceLabel.getStyle().set("text-align", "right");

        Icon cartIcon = new Icon(VaadinIcon.CART);
        cartIcon.getStyle().set("display", "inline-flex");
        cartIcon.getStyle().set("margin-left", "5px");

        Button addToCartBtn = new Button("Add to cart", cartIcon,
                e -> addToCart(product));
        addToCartBtn.setIconAfterText(true);
        addToCartBtn.getStyle().set("line-height", "0");
        addToCartBtn.setSizeFull();
        addToCartBtn.setHeight("100%");

        Item shortDescriptionItem = new Item("", product.getShortDescription());
        shortDescriptionItem.setAlignItems(Alignment.CENTER);

        TitleLabel titleLabel = new TitleLabel(product.getTitle());
        titleLabel.setFlexGrow(1);
        cartIcon.setId("category");

        PrimaryLabel emptyLabel = new PrimaryLabel("");
        emptyLabel.setFlexGrow(1);

        IconItem productIconItem = new IconItem(productImage == null ? defaultImage : productImage, "", "");
        productIconItem.setFlexGrow(1);

        Card card = new Card(
                // if you don't want the title to wrap you can set the whitespace = nowrap
                titleLabel,
//                emptyLabel,
                priceLabel,
                productIconItem,
                shortDescriptionItem,
//                defaultImage,
                new Actions(
                        addToCartBtn
//                        new ActionButton("Add to cart", event -> cartService.addToCart(VaadinService.getCurrentRequest(), product))
                )
        );
        card.setFlexGrow(1);
        card.setWidth("250px");
        card.setHeight("440px");
        card.getStyle().set("margin", "10px");
        card.getStyle().set("border", "1px solid black");
        card.getStyle().set("border-radius", "10px");
        card.getContent().getChildren().forEach(component -> {
            if (component.getId().isPresent()) {
                if (component.getId().get().equalsIgnoreCase("category")) {

                }
            }
        });
        return card;
    }

    private Image createImage(String src, String alt) {
        Image image = new Image(src, alt);
        image.setHeight("150px");
        image.setWidth("150px");
        image.getStyle().set("position", "relative");
        image.getStyle().set("left", "25%");
        image.getStyle().set("margin", "0");
        return image;
    }

    private List<Product> getAllProducts() {
        return productService.findAll(productFilter, Pageable.unpaged()).getContent();
    }

    private ConfigurableFilterDataProvider<Product, String, Category> getDataProvider(ProductService service) {
        DataProvider<Product, ProductFilter> dataProvider =
                DataProvider.fromFilteringCallbacks(query -> {
                    // getFilter returns Optional<String>
                    productFilter = query.getFilter().orElse(new ProductFilter());
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    page = service.findAll(
                            productFilter, PageRequest.of(offset, limit));
                    return page.getContent().stream();
                }, query -> {
                    productFilter = query.getFilter().orElse(new ProductFilter());
                    int offset = query.getOffset();
                    int limit = query.getLimit();
                    return service.countByFilter(productFilter, PageRequest.of(offset, limit));
                });

        return dataProvider
        .withConfigurableFilter(
                ProductFilter::new);
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    private void addToCart(Product product) {
        cartService.addToCart(VaadinService.getCurrentRequest(), product);
        incrementBadge(1);
    }

    private void incrementBadge(int cnt) {
        getUI().ifPresent(ui -> ui.getElement().getChildren().forEach(element -> {
            if (element.getTag().equalsIgnoreCase("paper-badge")) {
                String count = element.getAttribute("label");
                count = String.valueOf(Integer.valueOf(count) + cnt);
                element.setAttribute("label", count);
            }
        }));
    }
}
