package guru.sfg.brewery.security.permissions;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAuthority('order.pickUp') OR " +
        "(hasAuthority('customer.order.pickUp') " +
        " AND @beerOrderAuthenticationManager.customerIdMatches(authentication, #customerId ))")
public @interface OrderPickUpPermission {
}
