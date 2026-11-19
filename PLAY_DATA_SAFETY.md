# Google Play Data Safety — OnRoute

**App:** OnRoute (`letshangllc.onroute`)  
**Version:** 3.0.0 (versionCode 100)  
**Target SDK:** 36  
**Ads:** None (this app does not include an ads SDK)

Use this document when filling out the Play Console Data safety form.

---

## Overview

OnRoute helps users plan driving routes with multiple waypoints. It uses Google Maps, Places, and Directions APIs. The app does **not** show ads and does **not** use Firebase, analytics, or crash-reporting SDKs.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Approximate location | Yes | Yes (Google Maps/Places/Directions) | App functionality (map + routing) |
| Precise location | Yes | Yes (Google Maps/Places/Directions) | App functionality (My Location, current place, geocoding) |
| Search / place queries | Yes | Yes (Google Places) | Autocomplete and place lookup |
| Route waypoints / destinations | Yes | Yes (Google Directions) | Driving directions |
| Advertising ID | No | No | N/A — no ads SDK |
| Crash logs / diagnostics | No | No | N/A — no Crashlytics |

Location and place data are sent to Google over HTTPS to provide maps, autocomplete, and directions. The app does not store user accounts or sync route history to a developer-owned backend.

---

## Permissions That Need Play Declarations

| Permission | Why | Play notes |
|------------|-----|------------|
| `ACCESS_FINE_LOCATION` | My Location on the map, reverse geocoding start address, Places Find Current Place | Declare location data collection; explain in-app use |
| `ACCESS_COARSE_LOCATION` | Fallback when fine location is unavailable | Same as above |
| `INTERNET` | Maps tiles, Places, Directions API calls | Standard |
| `ACCESS_NETWORK_STATE` | Connectivity checks used by Google Play services | Standard |

No background location, no SMS/call log, no photo/media, no contacts.

---

## Third-Party SDKs

### Google Maps SDK for Android
- **Purpose:** Display map, markers, polylines
- **Data:** Device location (when permitted), map interactions
- **Privacy:** https://policies.google.com/privacy

### Places SDK for Android
- **Purpose:** Place autocomplete, fetch place by ID, approximate current place
- **Data:** Location (for current place), typed search queries, place IDs
- **Privacy:** https://policies.google.com/privacy

### Google Directions API (via Google-Directions-Android)
- **Purpose:** Driving routes between waypoints
- **Data:** Origin, destination, and waypoint coordinates
- **Privacy:** https://policies.google.com/privacy

---

## Data Security

- **In transit:** Yes — API traffic uses HTTPS (`usesCleartextTraffic="false"`).
- **At rest on device:** Location is used in memory for the current session; the app does not implement cloud account storage.
- **Deletion:** Users can clear app data / uninstall. There is no developer-hosted account to delete.

---

## Children's Privacy

This app is **not** directed at children under 13 (COPPA) or under 16 (GDPR age of consent in some regions).

---

## Play Console Form Responses (suggested)

1. **Does your app collect or share any of the required user data types?** Yes  
2. **Is all of the user data collected by your app encrypted in transit?** Yes  
3. **Do you provide a way for users to request that their data be deleted?** Yes (uninstall / clear app data; no cloud account)  
4. **Location — Approximate:** Collected + shared with Google for app functionality  
5. **Location — Precise:** Collected + shared with Google for app functionality  
6. **Ads:** App does not contain ads  

---

## Owner Follow-ups in Play Console

- Complete Data safety using the table above.
- Declare location permissions and describe My Location / routing use.
- Ensure the Google Cloud API key used in production is restricted to this package + signing cert SHA-1, with Maps SDK for Android, Places API, and Directions API enabled.
- No Families / ads policy notes beyond “app does not contain ads.”
