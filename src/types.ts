export type ClockStyleId = 
  | 'minimal'
  | 'editorial'
  | 'digital'
  | 'terminal'
  | 'typographic'
  | 'glass';

export type WidgetSize = 'small' | 'medium' | 'large';

export type FontOption = 
  | 'default'
  | 'modern-sans'
  | 'editorial-serif'
  | 'digital-mono'
  | 'code-mono'
  | 'expressive';

export type TextAlignment = 'left' | 'center' | 'right';

export type TextScale = 'compact' | 'balanced' | 'large';

export type CornerRadiusOption = 0 | 16 | 24 | 32 | 48;

export type BackgroundStyle = 'solid' | 'glass' | 'outline' | 'transparent' | 'mesh';

export type WidgetPadding = 'compact' | 'standard' | 'generous';

export interface AccentColor {
  id: string;
  name: string;
  hex: string;
  glanceHex: string;
  contrastText: string;
}

export interface ClockConfig {
  style: ClockStyleId;
  is24Hour: boolean;
  showSeconds: boolean;
  showDate: boolean;
  showWeekday: boolean;
  font: FontOption;
  alignment: TextAlignment;
  textSize: TextScale;
  cornerRadius: CornerRadiusOption;
  bgOpacity: number; // 0 - 100
  bgStyle: BackgroundStyle;
  accentColorId: string;
  padding: WidgetPadding;
  themeMode: 'dark' | 'light';
}

export interface ClockStyleDefinition {
  id: ClockStyleId;
  name: string;
  tagline: string;
  description: string;
  recommendedFont: FontOption;
  accentColorDefault: string;
  defaultCornerRadius: CornerRadiusOption;
  defaultBgStyle: BackgroundStyle;
  defaultBgOpacity: number;
}
