---
name: Kelasi Noir
colors:
  surface: '#f9f9ff'
  surface-dim: '#d3daef'
  surface-bright: '#f9f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f1f3ff'
  surface-container: '#e9edff'
  surface-container-high: '#e1e8fd'
  surface-container-highest: '#dce2f7'
  on-surface: '#141b2b'
  on-surface-variant: '#4c4546'
  inverse-surface: '#293040'
  inverse-on-surface: '#edf0ff'
  outline: '#7e7576'
  outline-variant: '#cfc4c5'
  surface-tint: '#5e5e5e'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#1b1b1b'
  on-primary-container: '#848484'
  inverse-primary: '#c6c6c6'
  secondary: '#585f6c'
  on-secondary: '#ffffff'
  secondary-container: '#dce2f3'
  on-secondary-container: '#5e6572'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#191c1e'
  on-tertiary-container: '#828486'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#e2e2e2'
  primary-fixed-dim: '#c6c6c6'
  on-primary-fixed: '#1b1b1b'
  on-primary-fixed-variant: '#474747'
  secondary-fixed: '#dce2f3'
  secondary-fixed-dim: '#c0c7d6'
  on-secondary-fixed: '#151c27'
  on-secondary-fixed-variant: '#404754'
  tertiary-fixed: '#e1e2e4'
  tertiary-fixed-dim: '#c5c6c8'
  on-tertiary-fixed: '#191c1e'
  on-tertiary-fixed-variant: '#444749'
  background: '#f9f9ff'
  on-background: '#141b2b'
  surface-variant: '#dce2f7'
typography:
  display-xl:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  headline-md:
    fontFamily: Inter
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 36px
  title-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 28px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-caps:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
  caption:
    fontFamily: Inter
    fontSize: 10px
    fontWeight: '500'
    lineHeight: 14px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  container-padding: 4rem
  element-gap: 1.25rem
  section-margin: 2.5rem
  input-padding-y: 0.75rem
  input-padding-x: 1rem
---

## Brand & Style

Kelasi Noir embodies a **Modern Corporate** aesthetic with strong **Minimalist** and **Glassmorphic** influences. The brand personality is professional, sophisticated, and authoritative, yet approachable for a modern educational context. 

The visual style relies on a high-contrast monochromatic base (pure blacks and whites) contrasted with soft, multi-layered depth. It targets a demographic that appreciates a premium "SaaS" feel—clean lines, generous whitespace, and subtle atmospheric details like backdrop blurs and abstract geometric overlays. The emotional response is one of clarity, focus, and structural reliability.

## Colors

The palette is strictly monochromatic to maintain a high-end editorial feel. 

- **Primary & Neutral:** Pure Black (#000000) is used for primary actions and headings to command attention. Grays range from Zinc to Slate to provide hierarchy without introducing hue.
- **Surface Strategy:** The UI uses a "Split Surface" approach. The functional side (forms) sits on a pure white background, while the inspirational side (branding) utilizes a deep obsidian black (#0C0C0C).
- **Accents:** Semantic colors for social brand icons (Google Blue, Facebook Blue) are the only deviations from the monochrome scale, used sparingly to aid recognition.

## Typography

The system utilizes **Inter** exclusively to lean into a systematic, utilitarian aesthetic. 

- **Hierarchy:** Dramatic contrast between large, bold headlines and small, uppercase labels. 
- **Readability:** Body text is kept at 14px for a dense, professional feel, using wide line-heights for comfortable scanning.
- **Letter Spacing:** Headlines use slight negative tracking for a tighter "locked-in" look, while labels use expanded tracking for better legibility at small sizes.

## Layout & Spacing

The layout follows a **Fixed Grid** philosophy within a centered 5XL container (1024px), utilizing a two-column split-panel design for desktop environments. 

- **Internal Padding:** Generous internal padding (64px/4rem) creates a premium sense of "air."
- **Rhythm:** A base-4 spacing scale is used. Consistent 20px (1.25rem) gaps exist between form fields, while larger 40px (2.5rem) blocks separate logical sections like the form and social login dividers.

## Elevation & Depth

Elevation is achieved through **Tonal Separation** and **Glassmorphism** rather than traditional drop shadows on every element.

- **Main Container:** Uses a 'shadow-2xl' (extra-diffused, large spread) to lift the entire application off the gradient background.
- **Glass Effects:** On dark surfaces, depth is created using `backdrop-filter: blur(10px)` combined with low-opacity white borders (10%) and fills (5%). This creates a layered, "pro" feel.
- **Atmospheric Depth:** Subtle radial blurs and border-stroke circles are used in the background of dark sections to provide a sense of infinite space without distracting from content.

## Shapes

The shape language is consistently **Rounded**, leaning towards a friendly but structured appearance.

- **Containers:** Large outer containers use a `3xl` (1.5rem / 24px) radius to soften the high-contrast aesthetic.
- **Interactive Elements:** Buttons and Input fields use a consistent `xl` (0.75rem / 12px) radius. 
- **Small Elements:** Checkboxes and social buttons maintain a slightly smaller `lg` (0.5rem / 8px) radius to appear more precise.

## Components

### Buttons
- **Primary:** Solid black background, white text, bold weight. Transition to a dark zinc color on hover. High-radius (12px).
- **Social/Outline:** Transparent background with a light gray (200) border. Subtle hover states (bg-gray-50).

### Input Fields
- **Text Inputs:** Light gray background (#F9FAFB) with a 1px border (#E5E7EB). Icons are positioned absolutely on the left, with text indented. Focus states use a crisp black border and ring.
- **Checkboxes:** Small, rounded-sm, black fill when active, paired with standard body-md text.

### Dividers
- Hairline (1px) borders in gray-200. Text labels within dividers are centered with background-color padding to "cut" the line.

### Cards & Glass Panels
- Feature cards inside dark sections should utilize the `glass-effect`. This includes a 1px white/10 border and a 10px backdrop blur to maintain legibility over abstract backgrounds.

### Avatars
- Small (32px), circular, with a ring-2 stroke that matches the container's background to create separation in stacked "facepile" layouts.