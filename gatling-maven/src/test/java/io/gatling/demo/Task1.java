package io.gatling.demo;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.util.Map;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.Choice;
import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class Task1 extends Simulation {

        FeederBuilder<String> dsTables = csv("DS_tables.csv").circular();
        FeederBuilder<String> dsChairs = csv("DS_chairs.csv").circular();
        FeederBuilder<String> dsLamps = csv("DS_lamps.csv").circular();
        FeederBuilder<String> dsUsers = csv("DS_users.csv").circular();

        private HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://wp")
                        .inferHtmlResources(AllowList(),
                                        DenyList(".*.png", ".*.jpg", ".*/wp-content/uploads/2023.*",
                                                        ".*/wp-includes/css/.*", ".*/wp-includes.*",
                                                        ".*/wp-content/plugins.*",
                                                        ".*/wp-content/themes.*",
                                                        ".*/favicon.ico"))
                        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .acceptEncodingHeader("gzip, deflate")
                        .acceptLanguageHeader("en-US,en;q=0.5")
                        .userAgentHeader(
                                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:133.0) Gecko/20100101 Firefox/133.0");

        private Map<CharSequence, String> headers_0 = Map.ofEntries(
                        Map.entry("Priority", "u=0, i"),
                        Map.entry("Upgrade-Insecure-Requests", "1"));

        private Map<CharSequence, String> headers_4 = Map.ofEntries(
                        Map.entry("Accept", "*/*"),
                        Map.entry("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"),
                        Map.entry("Origin", "http://wp"),
                        Map.entry("Priority", "u=0"),
                        Map.entry("X-Requested-With", "XMLHttpRequest"));

        private Map<CharSequence, String> headers_9 = Map.ofEntries(
                        Map.entry("Origin", "http://wp"),
                        Map.entry("Priority", "u=0, i"),
                        Map.entry("Upgrade-Insecure-Requests", "1"));

        private Map<CharSequence, String> headers_11 = Map.ofEntries(
                        Map.entry("Content-Type",
                                        "multipart/form-data; boundary=---------------------------4273566429787328532028452588"),
                        Map.entry("Origin", "http://wp"),
                        Map.entry("Priority", "u=0, i"),
                        Map.entry("Upgrade-Insecure-Requests", "1"));

        private ChainBuilder Open_app = exec(
                        http("Step_0_Open_app")
                                        .get("/")
                                        .disableFollowRedirect()
                                        .headers(headers_0)
                                        .check(status().is(200)));
        // .pause(1);

        private ChainBuilder Navigate_to_(String page, String requestNumber) {
                return exec(
                                http("Step_" + requestNumber + "_Navigate_to_" + page)
                                                .get("/" + page)
                                                .headers(headers_0)
                                                .check(status().is(200)))
                                .pause(1);

        }

        private ChainBuilder Open_product(String productType, String ProductNumber, String saveValueIn,
                        String requestNumber) {
                return exec(
                                http("Step_" + requestNumber + "_Open_product" + productType)
                                                .get("/products/" + productType + ProductNumber)
                                                .headers(headers_0)
                                                .check(regex("post_id.:\"(.{1,4})\",")
                                                                .saveAs("extractedProductId" + saveValueIn))
                                                .check(status().is(200)))
                                .pause(1);
        }

        private ChainBuilder Add_table_to_the_car = exec(
                        http("Step_3_Add_table_to_the_car")
                                        .post("/wp-admin/admin-ajax.php")
                                        .headers(headers_4)
                                        .formParam("action", "ic_add_to_cart")
                                        .formParam("add_cart_data",
                                                        "current_product=#{extractedProductId}&cart_content=&current_quantity=1")
                                        .formParam("cart_widget", "0")
                                        .formParam("cart_container", "0")
                                        .check(status().is(200)))
                        .pause(1);

        private ChainBuilder add_product(String product) {
                return exec(
                                http("Step_6_add_product_" + product)
                                                .post("/wp-admin/admin-ajax.php")
                                                .headers(headers_4)
                                                .formParam("action", "ic_add_to_cart")
                                                .formParam("add_cart_data",
                                                                "current_product=#{extractedProductId2}&cart_content=%7B%22#{extractedProductId}__%22%3A1%7D&current_quantity=1")
                                                .formParam("cart_widget", "0")
                                                .formParam("cart_container", "0")
                                                .check(status().is(200)))
                                .pause(1);
        }

        private ChainBuilder Navigate_to_cart = exec(
                        http("Step_7_Navigate_to_cart")
                                        .get("/cart")
                                        .headers(headers_0)
                                        .check(regex("data-price=\"(.{1,10})\"").find(0).saveAs("price"))
                                        .check(regex("data-price=\"(.{1,10})\"").find(1).saveAs("price2"))
                                        .check(regex("total_net\">(.{1,10})<").saveAs("total"))
                                        .check(regex("value=\"(.{11,16})\" name=\"trans_id").saveAs("transactionId"))
                                        .check(status().is(200)))
                        .pause(1);

        private ChainBuilder Click_place_and_order = exec(
                        http("Step_8_Click_place_and_order")
                                        .post("/checkout")
                                        .headers(headers_9)
                                        .formParam("cart_content",
                                                        "{\"#{extractedProductId}__\":1,\"#{extractedProductId2}__\":1}")
                                        .formParam("p_id[]", "#{extractedProductId}__")
                                        .formParam("p_quantity[]", "1")
                                        .formParam("p_id[]", "#{extractedProductId2}__")
                                        .formParam("p_quantity[]", "1")
                                        .formParam("total_net", "#{total}")
                                        .formParam("trans_id", "#{transactionId}")
                                        .formParam("shipping", "order")
                                        .check(status().is(200)))
                        .pause(1);

        private ChainBuilder Fill_out_form = exec(
                        http("Step_9")
                                        .post("/wp-admin/admin-ajax.php")
                                        .headers(headers_4)
                                        .formParam("action", "ic_state_dropdown")
                                        .formParam("country_code", "#{countryCod}")
                                        .formParam("state_code", "")
                                        .check(status().is(200)))
                        .pause(1);

        private ChainBuilder Place_order = exec(
                        http("Step_10")
                                        .post("/checkout")
                                        .headers(headers_11)
                                        .disableFollowRedirect()
                                        .body(StringBody(
                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"ic_formbuilder_redirect\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "http://wp/thank-you\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_content\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "{\"#{extractedProductId}__\":1,\"#{extractedProductId}2__\":1}\r\n"
                                                                        + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"product_price_#{extractedProductId}__\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{price}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"product_price_#{extractedProductId2}__\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{price2}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"total_net\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{total}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"trans_id\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{transactionId}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"shipping\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "order\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_content\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "{\"#{extractedProductId}__\":1,\"#{extractedProductId2}__\":1}\r\n"
                                                                        + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_type\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "order\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_inside_header_1\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "<b>BILLING ADDRESS</b>\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_company\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{country}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_name\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{fullName}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_address\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{address}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_postal\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{postalCode}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_city\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{city}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_country\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{state}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_state\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "CO_DC\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_phone\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{phone}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_email\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "#{email}\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_comment\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_inside_header_2\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "<b>DELIVERY ADDRESS</b> (FILL ONLY IF DIFFERENT FROM THE BILLING ADDRESS)\r\n"
                                                                        + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_company\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_name\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_address\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_postal\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_city\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_country\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_state\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_phone\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_email\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_s_comment\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588\r\n"
                                                                        + //
                                                                        "Content-Disposition: form-data; name=\"cart_submit\"\r\n"
                                                                        + //
                                                                        "\r\n" + //
                                                                        "Place Order\r\n" + //
                                                                        "-----------------------------4273566429787328532028452588--\r\n"
                                                                        + //
                                                                        ""

                                        ))
                                        .check(status().is(200)));
        // .pause(1);

        private ScenarioBuilder scn = scenario("Task1")
                        .repeat(5, "i").on(
                                        exec(
                                                        feed(dsTables),
                                                        feed(dsChairs),
                                                        feed(dsLamps),
                                                        feed(dsUsers),
                                                        // Open_app,
                                                        Open_app,
                                                        // Navigate_to_tables,
                                                        Navigate_to_("#{object_type}", "1"),
                                                        // Open_table,
                                                        Open_product("#{specific_type}", "#{product_number}", "", "2"),
                                                        // Add_table_to_the_car,
                                                        Add_table_to_the_car,
                                                        // Navigate_to_chairs,
                                                        // Open_a_chair,
                                                        // add_chair,
                                                        randomSwitch().on(
                                                                        new Choice.WithWeight(50.0, exec(
                                                                                        Navigate_to_("#{object_type_1}",
                                                                                                        "4"),
                                                                                        Open_product("#{specific_type_1}",
                                                                                                        "#{product_number_1}",
                                                                                                        "2", "5"),
                                                                                        add_product("#{object_type_1}"))),
                                                                        new Choice.WithWeight(50.0, exec(
                                                                                        Navigate_to_("#{object_type_2}",
                                                                                                        "4"),
                                                                                        Open_product("#{specific_type_2}",
                                                                                                        "#{product_number_2}",
                                                                                                        "2", "5"),
                                                                                        add_product("#{object_type_2}")))),

                                                        randomSwitch().on(
                                                                        new Choice.WithWeight(30.0, exec(
                                                                                        // Navigate_to_cart,
                                                                                        Navigate_to_cart,
                                                                                        // Click_place_an_order,
                                                                                        Click_place_and_order,
                                                                                        // Fill_out_form,
                                                                                        Fill_out_form,
                                                                                        // Place_order,
                                                                                        Place_order)),
                                                                        new Choice.WithWeight(40.0, exec(
                                                                                        // Navigate_to_cart,
                                                                                        Navigate_to_cart,
                                                                                        // Navigate_to_checkout
                                                                                        // directly,
                                                                                        Navigate_to_("checkout", "12"),
                                                                                        Fill_out_form,
                                                                                        // Place_order,
                                                                                        Place_order)),
                                                                        new Choice.WithWeight(30.0, exec(
                                                                                        Navigate_to_("thank-you",
                                                                                                        "13"))))));

        {
                setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
        }

}
