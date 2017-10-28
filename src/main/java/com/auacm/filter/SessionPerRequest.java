package com.auacm.filter;

import org.springframework.orm.hibernate5.support.OpenSessionInViewFilter;
import org.springframework.stereotype.Component;

//@Component
public class SessionPerRequest extends OpenSessionInViewFilter {
}
