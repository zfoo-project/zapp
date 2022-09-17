const GA_TRACKING_ID = process.env.VUE_APP_GOOGLE_ANALYTICS;

export function event(eventCategory, eventAction, eventLabel, eventValue) {
    if (window.gtag) {
        window.gtag('event', eventAction, {
            event_category: eventCategory,
            event_label: eventLabel,
            value: eventValue,
            send_to: GA_TRACKING_ID
        });
    }
}

export function page(title, page, location) {
    if (window.gtag) {
        window.gtag('config', GA_TRACKING_ID, {
            page_title: title,
            page_path: page,
            page_location: location
        });
    }
}
