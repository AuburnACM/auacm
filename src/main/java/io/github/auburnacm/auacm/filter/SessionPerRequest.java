package io.github.auburnacm.auacm.filter;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;

/**
 * Created by Mac on 9/13/17.
 */
@Component
public class SessionPerRequest extends OpenSessionInViewFilter {
}
