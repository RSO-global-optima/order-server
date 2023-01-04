package team.globaloptima;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@RequestScoped
public class OrderService {

    @PersistenceContext(unitName = "team-globaloptima-order")
    private EntityManager em;

    public Order getOrder(Integer orderId) {
        return em.find(Order.class, orderId);
    }

    public List<Order> getOrders() {
        List<Order> orders = em
                .createNamedQuery("Order.findOrders", Order.class)
                .getResultList();

        return orders;
    }

    @Transactional
    public void addCustomerAddress(Order order, Customer customer) {
        em.createQuery("UPDATE Order o SET o.customerAddress = :custAdr WHERE o.id = :ordId")
                .setParameter("custAdr", customer.getAddress().toString())
                .setParameter("ordId", order.getId())
                .executeUpdate();
    }

    @Transactional
    public void addSupplierAddress(Order order, Supplier supplier) {
        em.createQuery("UPDATE Order o SET o.supplierAddress = :suplAdr WHERE o.id = :ordId")
                .setParameter("suplAdr", supplier.getAddress().toString())
                .setParameter("ordId", order.getId())
                .executeUpdate();
    }

    @Transactional
    public List<Order> getPendingOrdersToCook(Integer supplierId) {
        List<Order> orders = em.createQuery("SELECT o FROM Order o WHERE o.suppliedStatus = 'Not cooking' AND o.supplierId = :supId", Order.class)
                .setParameter("supId", supplierId)
                .getResultList();

        return orders;
    }
    @Transactional
    public List<Order> getPendingOrdersToDeliver() {
        List<Order> orders = em.createQuery("SELECT o FROM Order o WHERE o.suppliedStatus = 'Cooked' AND o.deliveredStatus = 'Not delivering'", Order.class)
                .getResultList();

        return orders;
    }


    @Transactional
    public void saveOrder(Order order) {
        if (order != null) {
            em.persist(order);
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteOrder(Integer orderId) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            em.remove(order);
        }
    }

    //PUT methods
    @Transactional
    public void cookOrder(Order order, Integer estTime) {
        em.createQuery("UPDATE Order o SET o.suppliedStatus = 'Cooking', o.estimatedCookingMinutes = :estTime WHERE o.id = :ordId")
                .setParameter("ordId", order.getId()).setParameter("estTime", estTime)
                .executeUpdate();
    }

    @Transactional
    public void pickOrder(Order order, Integer estTime) {
        em.createQuery("UPDATE Order o SET o.deliveredStatus = 'Delivering', o.estimatedDeliveryMinutes = :estTime WHERE o.id = :ordId")
                .setParameter("ordId", order.getId()).setParameter("estTime", estTime)
                .executeUpdate();
    }

    @Transactional
    public void rejectOrder(Order order) {
        em.createQuery("UPDATE Order o SET o.suppliedStatus = 'Rejected' WHERE o.id = :ordId")
                .setParameter("ordId", order.getId())
                .executeUpdate();
    }

    @Transactional
    public void cookedOrder(Order order) {
        em.createQuery("UPDATE Order o SET o.suppliedStatus = 'Cooked' WHERE o.id = :ordId")
                .setParameter("ordId", order.getId())
                .executeUpdate();
    }

    @Transactional
    public void deliveredOrder(Order order) {
        em.createQuery("UPDATE Order o SET o.deliveredStatus = 'Delivered' WHERE o.id = :ordId")
                .setParameter("ordId", order.getId())
                .executeUpdate();
    }

}
