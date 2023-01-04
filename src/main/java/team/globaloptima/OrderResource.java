package team.globaloptima;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder; // for API calls
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import team.globaloptima.Customer;
import team.globaloptima.Supplier;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("orders")
public class OrderResource {

    @Inject
    OrderService orderBean;

    private String customerServiceUrl = "http://localhost:8080";

    private String supplierServiceUrl = "http://localhost:8082";

    public String getCustomrtServiceUrl() {
        return this.customerServiceUrl;
    }

    @GET
    @Operation(summary = "Get order list", description = "Returns a list of all orders.")
    @APIResponses({
            @APIResponse(description = "List of orders", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Order.class,
                            type = SchemaType.ARRAY)))
    })
    public Response getAllOrders() {
        List<Order> orders = orderBean.getOrders();
        return Response.ok(orders).build();
    }

    @GET
    @Operation(summary = "Get order details", description = "Return details of one order with selected id")
    @APIResponses({
            @APIResponse(description = "Order", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Order.class)))
    })
    @Path("{orderId}")
    public Response getOrder(@PathParam("orderId") Integer orderId) {
        Order order = orderBean.getOrder(orderId);
        return order != null
                ? Response.ok(order).build()
                : Response.ok(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Operation(summary = "Create new order", description = "Create new order")
    @APIResponses({
            @APIResponse(responseCode = "204",
                    description = "New order created"
            )
    })
    public Response addNewOrder(@RequestBody(
            description = "JSON object with Order data",
            required = true, content = @Content(
            schema = @Schema(implementation = Order.class))) Order order) {
        orderBean.saveOrder(order);

        // request for customer adress

        Response customerResponse = ClientBuilder.newClient()
                .target(customerServiceUrl).path("v1").path("customers").path(order.getCustomerId().toString())
                .request().get();

        if (!customerResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Customer customer = customerResponse.readEntity(Customer.class);
        orderBean.addCustomerAddress(order, customer);

        // request for supplier address

        Response supplierResponse = ClientBuilder.newClient()
                .target(supplierServiceUrl).path("v1").path("suppliers").path(order.getSupplierId().toString())
                .request().get();

        if (!supplierResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Supplier supplier = supplierResponse.readEntity(Supplier.class);
        orderBean.addSupplierAddress(order, supplier);

        return Response.noContent().build();
    }

    @DELETE
    @Operation(description = "Delete Order", summary = "Delete order")
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Order successfully deleted."
            )
    })
    @Path("{orderId}")
    public Response deleteOrder(@Parameter(description = "Order ID.", required = true)
                                   @PathParam("orderId") Integer orderId) {
        orderBean.deleteOrder(orderId);
        return Response.noContent().build();
    }

    @GET
    @Operation(description = "Get pending orders (to cook)", summary = "Get orders waiting to be prepared.")
    @APIResponses({
            @APIResponse(description = "List of pending orders to cook", responseCode = "200",
                            content = @Content(schema = @Schema(implementation = Order.class,
                                    type = SchemaType.ARRAY))
            )
    })
    @Path("pending")
    public Response getPendingOrders(
            @DefaultValue("-1") @QueryParam("supplier") Integer supplierId,
            @DefaultValue("true") @QueryParam("delivery") boolean isDelivery
    ) {

        List<Order> orders = null;

        if (!supplierId.equals(-1)) {
            orders = orderBean.getPendingOrdersToCook(supplierId);
        }
        else if (isDelivery) {
            orders = orderBean.getPendingOrdersToDeliver();
        } else {
            orders = orderBean.getOrders();
        }

        System.out.println(orders);

        return orders != null
                ? Response.ok(orders).build()
                : Response.ok(Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Operation(summary = "Accept order", description = "Accepting order")
    @APIResponses({
            @APIResponse(responseCode = "204",
                    description = "Order accepted"
            )
    })
    @Path("pending/{orderId}")
    public Response acceptOrder(
            @PathParam("orderId") Integer orderId,
            @DefaultValue("false") @QueryParam("accept") boolean isAccept,
            @DefaultValue("false") @QueryParam("reject") boolean isReject,
            @DefaultValue("false") @QueryParam("done") boolean isDone,
            @DefaultValue("false") @QueryParam("deliver") boolean isDeliver,
            @DefaultValue("false") @QueryParam("cook") boolean isCooking,
            @DefaultValue("0") @QueryParam("estTime") Integer estTime
    ) {
        Order order = orderBean.getOrder(orderId);

        if (isCooking) {
            if (isAccept) {
                orderBean.cookOrder(order, estTime);
            } else if (isReject) {
                orderBean.rejectOrder(order);
            } else if (isDone) {
                orderBean.cookedOrder(order);
            }
        } else if (isDeliver) {
            if (isAccept) {
                orderBean.pickOrder(order, estTime);
            } else if (isDone) {
                orderBean.deliveredOrder(order);
            }
        }

        return Response.noContent().build();
    }
}
