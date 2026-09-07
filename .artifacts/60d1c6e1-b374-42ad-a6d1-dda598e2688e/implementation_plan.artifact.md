# Implementation Plan: UI Overhaul to Match Figma Prototype

This plan outlines the changes needed to bring the "ClassFind" app's UI in line with the provided Figma prototype. This involves updating the theme, implementing global navigation, and refactoring several screens for a modern, grid-based, and icon-rich layout.

## User Review Required

> [!IMPORTANT]
> The prototype includes icons and images (e.g., logo, building icons) that are not currently in the project. I will use standard Material Icons or placeholders for these.

> [!NOTE]
> I will implement a Bottom Navigation Bar that will be visible on the main screens (Home, Map, Profile) as shown in the prototype.

## Proposed Changes

### Theming

#### [MODIFY] [Color.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/ui/theme/Color.kt)
Update the color palette to match the purple primary color seen in the Figma prototype.
- Set `Purple40` to a deep purple (`0xFF6200EE` or similar).
- Add background and surface colors that align with the prototype's clean, white/light-grey look.

---

### Navigation & Layout

#### [MODIFY] [MainActivity.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/MainActivity.kt)
- Introduce a `Scaffold` to wrap the `ClassFindApp` content.
- Implement a `BottomNavigationBar` with tabs for Home, Map, and Profile.
- Manage screen transitions between these main tabs and sub-screens (Search, Details).

---

### Screens

#### [MODIFY] [LoginScreen.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/Screens/LoginScreen.kt)
- Update layout to center the login form.
- Add a placeholder for the "Classroom Locator" logo.
- Stylize text fields and the "Sign In" button to match the rounded, purple design.

#### [MODIFY] [HomeScreen.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/Screens/HomeScreen.kt)
- Add a top search bar.
- Replace the vertical list of buttons with a 2x2 grid of cards: "Find Classroom", "Campus Map", "Buildings", and "Saved".
- Implement a "Recent Searches" section with list items containing icons and secondary text.

#### [MODIFY] [ProfileScreen.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/Screens/ProfileScreen.kt)
- Create a header section with a large profile icon, student name, and major.
- Replace the student info card with a list of navigation items (e.g., "My Bookings", "Saved Locations") each with a leading icon and a trailing arrow.
- Style the "Logout" button as a red text button or outlined button.

#### [MODIFY] [SearchScreen.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/Screens/SearchScreen.kt)
- Refine the list items to match Figma: add a building icon and distance indicator (e.g., "120m").
- Add filter chips ("Filter", "Nearest", "Floor") below the search bar.

#### [MODIFY] [ClassroomDetailsScreen.kt](file:///D:/Education/Third Year First Sem/CTE308/ClassFind/app/src/main/java/com/example/classfind/Screens/ClassroomDetailsScreen.kt)
- Add a visual representation of the classroom layout.
- Use icons for "Capacity", "Type", and "Facilities".

## Verification Plan

### Manual Verification
1. **Visual Check**: Deploy the app and compare each screen side-by-side with the Figma screenshot.
2. **Navigation Flow**: Ensure the Bottom Navigation Bar works correctly and maintains state where appropriate.
3. **Responsiveness**: Verify that the grid and list layouts look good on the emulator.
