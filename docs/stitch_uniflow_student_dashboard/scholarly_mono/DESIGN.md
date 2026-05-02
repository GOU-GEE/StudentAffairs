# Design System Specification: The Intellectual Monolith

## 1. Overview & Creative North Star

**Creative North Star: The Digital Gallery**
This design system is not a utility; it is a curated environment. Moving beyond the "standard SaaS" aesthetic, we aim for a "Digital Gallery" feel—where information is treated as art, and the interface serves as the pristine gallery wall. By leveraging an ultra-refined monochromatic palette, we prioritize content clarity and intellectual rigor.

The system breaks the "template" look through **intentional breathing room** and **tonal layering**. We reject the clutter of traditional dashboards in favor of a Bento Box architecture that feels architectural, rhythmic, and high-end. It is a signature experience that values the silence between elements as much as the elements themselves.

---

## 2. Colors & Surface Philosophy

The palette is strictly monochromatic, using saturation only as a functional "flare" for critical status indicators.

### The "No-Line" Rule
To achieve a premium editorial feel, designers are prohibited from using high-contrast 1px solid borders for general sectioning. Boundaries must be defined through:
1.  **Background Shifts:** Placing a `surface-container-low` card on a `surface` background.
2.  **Tonal Transitions:** Using subtle variations in gray to imply structure without the "boxiness" of traditional lines.

### Surface Hierarchy & Nesting
Treat the UI as a physical stack of fine paper. 
*   **Base:** `surface` (#f7f9fb) is your canvas.
*   **Secondary Layers:** Use `surface-container-low` for large sectioning.
*   **Actionable Elements:** Use `surface-container-lowest` (#ffffff) for primary cards to create a "lifted" effect.
*   **The "Glass & Gradient" Rule:** Floating elements (modals, popovers) must use Glassmorphism. Apply a semi-transparent `surface` color with a `20px` backdrop-blur. Main CTAs should utilize a subtle linear gradient from `primary` (#000000) to `primary_container` (#3c3b3b) to provide a "graphite" depth that flat black lacks.

---

## 3. Typography: The Editorial Voice

We use **Inter** as our typographic engine, configured with fluid scaling to ensure legibility across all viewport densities.

*   **Display & Headline:** These are your "Architectural" levels. Use `display-lg` (3.5rem) with a letter-spacing of `-0.02em` for hero sections. This tight tracking creates an authoritative, custom-tailored look.
*   **Titles:** `title-lg` (1.375rem) serves as the primary header for Bento cells.
*   **Body:** Use `body-md` (0.875rem) for general utility. It provides a sophisticated, "compact" feel that allows more information to breathe.
*   **Labels:** `label-sm` (0.6875rem) should be used in ALL-CAPS with `0.05em` letter-spacing for metadata and status tags to differentiate from interactive text.

---

## 4. Elevation & Depth

Hierarchy is achieved through **Tonal Layering** rather than heavy drop shadows.

*   **The Layering Principle:** 
    *   Level 0 (Canvas): `surface`
    *   Level 1 (Bento Cells): `surface-container-low`
    *   Level 2 (Active Cards): `surface-container-lowest`
*   **Ambient Shadows:** For floating elements, use an ultra-diffused shadow. 
    *   *Spec:* `0 12px 40px rgba(25, 28, 30, 0.06)`. The shadow color is a tinted version of `on-surface` to mimic natural light.
*   **The "Ghost Border" Fallback:** If containment is required for accessibility, use a "Ghost Border": `outline_variant` (#c6c6c6) at **15% opacity**. This provides a hint of a boundary without disrupting the visual flow.
*   **The Interactive Grid:** The background features an interactive dot grid. Dots should be `outline_variant` at 20% opacity, spaced at 24px intervals, creating a structural "blueprint" feel.

---

## 5. Components

### Bento Cards
The core unit of the system.
*   **Shape:** `xl` (1.5rem/24px) or `lg` (1rem/16px) corner radius.
*   **Structure:** No internal dividers. Use `body-lg` for headlines and `body-sm` for supporting text, separated by 16px of vertical whitespace.

### Buttons
*   **Primary:** Solid `primary` (#000000). On hover, transition to `primary_fixed`. Use `md` (0.75rem) corner radius.
*   **Secondary:** `surface-container-high` background with `on-surface` text.
*   **Tertiary/Ghost:** Transparent background with a `Ghost Border` appearing only on hover.

### Status Chips
The only place for color.
*   **Error:** `error_container` background with `on_error_container` text.
*   **Success/Tertiary:** `tertiary_container` background with `on_tertiary_container` text.
*   **Shape:** `full` (9999px) for a "pill" look that contrasts against the rectangular Bento grid.

### Input Fields
*   **Background:** `surface-container-low`.
*   **Border:** `none` by default; a 1px `primary` border appears only on focus.
*   **Corner Radius:** `md` (0.75rem).

---

## 6. Do's and Don'ts

### Do
*   **Do** use asymmetrical spacing. A card can have 40px padding on the top/left and 24px on the bottom/right to create a sense of movement.
*   **Do** use Lucide icons at a consistent 1.25px or 1.5px stroke weight.
*   **Do** leverage "White Space as Content." If a section feels crowded, remove a border and add 16px of padding.

### Don't
*   **Don't** use pure grey `#808080`. Use our `secondary` and `surface-variant` tokens to maintain the cool, off-white architectural tone.
*   **Don't** use standard 90-degree corners. Everything interactive or container-based must use at least an `md` (0.75rem) radius.
*   **Don't** use dividers to separate list items. Use a 1px height shift in background color or simply 12px of vertical gap.