import React from 'react';
import {
  BackgroundStyle,
  ClockConfig,
  ClockStyleId,
  CornerRadiusOption,
  FontOption,
  TextAlignment,
  TextScale,
  WidgetPadding,
} from '../types';
import { ACCENT_COLORS, CLOCK_STYLES, DEFAULT_CONFIGS } from '../data/constants';
import {
  RotateCcw,
  Check,
  AlignLeft,
  AlignCenter,
  AlignRight,
  Clock,
  Type,
  Layers,
  Palette,
} from 'lucide-react';

interface CustomizerPanelProps {
  config: ClockConfig;
  onChange: (newConfig: ClockConfig) => void;
  onResetDefaults: () => void;
  onSave: () => void;
}

export const CustomizerPanel: React.FC<CustomizerPanelProps> = ({
  config,
  onChange,
  onResetDefaults,
  onSave,
}) => {
  const updateConfig = (patch: Partial<ClockConfig>) => {
    onChange({ ...config, ...patch });
  };

  const currentStyleDef =
    CLOCK_STYLES.find((s) => s.id === config.style) || CLOCK_STYLES[0];

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex items-center justify-between pb-3 border-b border-gray-100">
        <div>
          <h3 className="text-base font-bold text-gray-900 flex items-center gap-2">
            <span>Style Settings</span>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 font-bold font-mono border border-blue-200/70">
              {currentStyleDef.name}
            </span>
          </h3>
          <p className="text-xs text-gray-400 mt-0.5">
            Fine-tune every visual dimension of your home-screen widget.
          </p>
        </div>

        <button
          id="btn-reset-defaults"
          onClick={onResetDefaults}
          title="Reset to Style Default"
          className="flex items-center gap-1 text-xs text-gray-600 hover:text-gray-900 py-1.5 px-3 rounded-xl bg-gray-100 hover:bg-gray-200 border border-gray-200/80 font-medium transition-colors"
        >
          <RotateCcw className="w-3.5 h-3.5" />
          <span>Defaults</span>
        </button>
      </div>

      {/* 1. Style Switcher Chips */}
      <div className="space-y-2">
        <label className="text-xs font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1.5">
          <Layers className="w-3.5 h-3.5 text-gray-400" />
          <span>Base Aesthetic</span>
        </label>
        <div className="grid grid-cols-3 gap-2">
          {CLOCK_STYLES.map((st) => (
            <button
              key={st.id}
              id={`style-chip-${st.id}`}
              onClick={() => {
                const defaults = DEFAULT_CONFIGS[st.id];
                onChange({
                  ...defaults,
                  is24Hour: config.is24Hour,
                  showSeconds: config.showSeconds,
                });
              }}
              className={`py-2 px-3 rounded-2xl text-xs font-medium border text-left transition-all ${
                config.style === st.id
                  ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20 shadow-sm'
                  : 'border-gray-200 bg-gray-50/70 text-gray-600 hover:text-gray-900 hover:bg-white'
              }`}
            >
              <div>{st.name}</div>
              <div className="text-[10px] text-gray-400 line-clamp-1">{st.tagline}</div>
            </button>
          ))}
        </div>
      </div>

      {/* 2. Time & Format Options */}
      <div className="space-y-3 pt-3 border-t border-gray-100">
        <label className="text-xs font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1.5">
          <Clock className="w-3.5 h-3.5 text-gray-400" />
          <span>Time & Date Elements</span>
        </label>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
          {/* 12 / 24 Hour Toggle */}
          <button
            id="toggle-24h"
            onClick={() => updateConfig({ is24Hour: !config.is24Hour })}
            className={`p-2.5 rounded-2xl border text-xs font-medium flex flex-col gap-1 transition-all text-left ${
              config.is24Hour
                ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                : 'border-gray-200 bg-gray-50/70 text-gray-700 hover:bg-white'
            }`}
          >
            <span className="text-[11px] text-gray-400">Hour Format</span>
            <span className="font-semibold">{config.is24Hour ? '24-Hour (14:30)' : '12-Hour (2:30)'}</span>
          </button>

          {/* Show Seconds */}
          <button
            id="toggle-seconds"
            onClick={() => updateConfig({ showSeconds: !config.showSeconds })}
            className={`p-2.5 rounded-2xl border text-xs font-medium flex flex-col gap-1 transition-all text-left ${
              config.showSeconds
                ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                : 'border-gray-200 bg-gray-50/70 text-gray-700 hover:bg-white'
            }`}
          >
            <span className="text-[11px] text-gray-400">Seconds Tick</span>
            <span className="font-semibold">{config.showSeconds ? 'Visible' : 'Hidden'}</span>
          </button>

          {/* Show Date */}
          <button
            id="toggle-date"
            onClick={() => updateConfig({ showDate: !config.showDate })}
            className={`p-2.5 rounded-2xl border text-xs font-medium flex flex-col gap-1 transition-all text-left ${
              config.showDate
                ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                : 'border-gray-200 bg-gray-50/70 text-gray-700 hover:bg-white'
            }`}
          >
            <span className="text-[11px] text-gray-400">Date Display</span>
            <span className="font-semibold">{config.showDate ? 'Visible' : 'Hidden'}</span>
          </button>

          {/* Show Weekday */}
          <button
            id="toggle-weekday"
            onClick={() => updateConfig({ showWeekday: !config.showWeekday })}
            className={`p-2.5 rounded-2xl border text-xs font-medium flex flex-col gap-1 transition-all text-left ${
              config.showWeekday
                ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                : 'border-gray-200 bg-gray-50/70 text-gray-700 hover:bg-white'
            }`}
          >
            <span className="text-[11px] text-gray-400">Weekday Tag</span>
            <span className="font-semibold">{config.showWeekday ? 'Visible' : 'Hidden'}</span>
          </button>
        </div>
      </div>

      {/* 3. Typography & Hierarchy */}
      <div className="space-y-3 pt-3 border-t border-gray-100">
        <label className="text-xs font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1.5">
          <Type className="w-3.5 h-3.5 text-gray-400" />
          <span>Typography & Alignment</span>
        </label>

        {/* Font Family Selector */}
        <div className="space-y-1.5">
          <span className="text-xs text-gray-500">Font Family</span>
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
            {[
              { id: 'modern-sans', label: 'Modern Sans', styleDesc: 'Clean Geometric' },
              { id: 'editorial-serif', label: 'Editorial Serif', styleDesc: 'High Contrast' },
              { id: 'digital-mono', label: 'Digital Matrix', styleDesc: 'Precision Tech' },
              { id: 'code-mono', label: 'Code Monospace', styleDesc: 'Developer Terminal' },
              { id: 'expressive', label: 'Expressive Display', styleDesc: 'Bold Typographic' },
              { id: 'default', label: 'System Default', styleDesc: 'Standard Roboto' },
            ].map((f) => (
              <button
                key={f.id}
                id={`font-opt-${f.id}`}
                onClick={() => updateConfig({ font: f.id as FontOption })}
                className={`py-2 px-3 rounded-xl border text-left text-xs transition-all ${
                  config.font === f.id
                    ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                    : 'border-gray-200 bg-gray-50/70 text-gray-600 hover:text-gray-900 hover:bg-white'
                }`}
              >
                <div>{f.label}</div>
                <div className="text-[10px] text-gray-400">{f.styleDesc}</div>
              </button>
            ))}
          </div>
        </div>

        {/* Text Alignment & Scale */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          {/* Alignment */}
          <div className="space-y-1.5">
            <span className="text-xs text-gray-500">Text Alignment</span>
            <div className="flex items-center gap-1.5 p-1 rounded-xl bg-gray-100 border border-gray-200">
              {[
                { id: 'left', icon: AlignLeft, label: 'Left' },
                { id: 'center', icon: AlignCenter, label: 'Center' },
                { id: 'right', icon: AlignRight, label: 'Right' },
              ].map((al) => {
                const Icon = al.icon;
                return (
                  <button
                    key={al.id}
                    id={`align-${al.id}`}
                    onClick={() => updateConfig({ alignment: al.id as TextAlignment })}
                    className={`flex-1 py-1.5 rounded-lg flex items-center justify-center gap-1.5 text-xs transition-all ${
                      config.alignment === al.id
                        ? 'bg-white text-gray-900 font-bold shadow-sm'
                        : 'text-gray-500 hover:text-gray-900'
                    }`}
                  >
                    <Icon className="w-3.5 h-3.5" />
                    <span>{al.label}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Text Size */}
          <div className="space-y-1.5">
            <span className="text-xs text-gray-500">Text Scale</span>
            <div className="flex items-center gap-1.5 p-1 rounded-xl bg-gray-100 border border-gray-200">
              {(['compact', 'balanced', 'large'] as TextScale[]).map((sz) => (
                <button
                  key={sz}
                  id={`text-size-${sz}`}
                  onClick={() => updateConfig({ textSize: sz })}
                  className={`flex-1 py-1.5 rounded-lg capitalize text-xs transition-all ${
                    config.textSize === sz
                      ? 'bg-white text-gray-900 font-bold shadow-sm'
                      : 'text-gray-500 hover:text-gray-900'
                  }`}
                >
                  {sz}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* 4. Surface, Frame & Opacity */}
      <div className="space-y-3 pt-3 border-t border-gray-100">
        <label className="text-xs font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1.5">
          <Layers className="w-3.5 h-3.5 text-gray-400" />
          <span>Surface & Border Geometry</span>
        </label>

        {/* Background Style */}
        <div className="space-y-1.5">
          <span className="text-xs text-gray-500">Background Style</span>
          <div className="grid grid-cols-3 sm:grid-cols-5 gap-1.5">
            {[
              { id: 'solid', label: 'Solid Card' },
              { id: 'glass', label: 'Frosted Glass' },
              { id: 'outline', label: 'Outlined' },
              { id: 'transparent', label: 'Pure Clear' },
              { id: 'mesh', label: 'Subtle Mesh' },
            ].map((bg) => (
              <button
                key={bg.id}
                id={`bg-style-${bg.id}`}
                onClick={() => updateConfig({ bgStyle: bg.id as BackgroundStyle })}
                className={`py-2 px-2 rounded-xl border text-center text-xs transition-all ${
                  config.bgStyle === bg.id
                    ? 'border-blue-600 bg-blue-50/70 text-blue-900 font-bold ring-1 ring-blue-500/20'
                    : 'border-gray-200 bg-gray-50/70 text-gray-600 hover:text-gray-900 hover:bg-white'
                }`}
              >
                {bg.label}
              </button>
            ))}
          </div>
        </div>

        {/* Opacity Slider */}
        <div className="space-y-1.5 pt-1">
          <div className="flex items-center justify-between text-xs text-gray-500">
            <span>Background Opacity</span>
            <span className="font-mono text-gray-900 font-bold">{config.bgOpacity}%</span>
          </div>
          <input
            type="range"
            id="slider-bg-opacity"
            min="0"
            max="100"
            step="5"
            value={config.bgOpacity}
            onChange={(e) => updateConfig({ bgOpacity: Number(e.target.value) })}
            className="w-full accent-blue-600 bg-gray-200 h-1.5 rounded-lg appearance-none cursor-pointer"
          />
        </div>

        {/* Corner Radius & Padding */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-1">
          {/* Corner Radius */}
          <div className="space-y-1.5">
            <span className="text-xs text-gray-500">Corner Radius</span>
            <div className="flex items-center gap-1.5 p-1 rounded-xl bg-gray-100 border border-gray-200">
              {([0, 16, 24, 32, 48] as CornerRadiusOption[]).map((r) => (
                <button
                  key={r}
                  id={`radius-${r}`}
                  onClick={() => updateConfig({ cornerRadius: r })}
                  className={`flex-1 py-1.5 rounded-lg text-xs font-mono transition-all ${
                    config.cornerRadius === r
                      ? 'bg-white text-gray-900 font-bold shadow-sm'
                      : 'text-gray-500 hover:text-gray-900'
                  }`}
                >
                  {r === 48 ? 'Pill' : `${r}dp`}
                </button>
              ))}
            </div>
          </div>

          {/* Padding */}
          <div className="space-y-1.5">
            <span className="text-xs text-gray-500">Widget Padding</span>
            <div className="flex items-center gap-1.5 p-1 rounded-xl bg-gray-100 border border-gray-200">
              {(['compact', 'standard', 'generous'] as WidgetPadding[]).map((p) => (
                <button
                  key={p}
                  id={`pad-${p}`}
                  onClick={() => updateConfig({ padding: p })}
                  className={`flex-1 py-1.5 rounded-lg capitalize text-xs transition-all ${
                    config.padding === p
                      ? 'bg-white text-gray-900 font-bold shadow-sm'
                      : 'text-gray-500 hover:text-gray-900'
                  }`}
                >
                  {p}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* 5. Accent Palette */}
      <div className="space-y-3 pt-3 border-t border-gray-100">
        <label className="text-xs font-bold uppercase tracking-wider text-gray-400 flex items-center gap-1.5">
          <Palette className="w-3.5 h-3.5 text-gray-400" />
          <span>Color & Accent Highlights</span>
        </label>
        <div className="grid grid-cols-4 sm:grid-cols-8 gap-2">
          {ACCENT_COLORS.map((c) => {
            const isSelected = config.accentColorId === c.id;
            return (
              <button
                key={c.id}
                id={`accent-swatch-${c.id}`}
                onClick={() => updateConfig({ accentColorId: c.id })}
                className={`flex flex-col items-center gap-1.5 p-2 rounded-xl border transition-all ${
                  isSelected
                    ? 'border-blue-600 bg-blue-50/80 ring-2 ring-blue-500/30 shadow-sm'
                    : 'border-gray-200 bg-gray-50/70 hover:border-gray-300'
                }`}
              >
                <div
                  className="w-6 h-6 rounded-full flex items-center justify-center shadow-sm border border-black/10"
                  style={{ backgroundColor: c.hex }}
                >
                  {isSelected && <Check className="w-3.5 h-3.5 text-white stroke-[3] drop-shadow" />}
                </div>
                <span className="text-[10px] text-gray-500 truncate max-w-[56px] text-center font-medium">
                  {c.name.split(' ')[0]}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Save Button */}
      <div className="pt-4 border-t border-gray-100">
        <button
          id="btn-save-configuration"
          onClick={onSave}
          className="w-full py-4 bg-black text-white hover:bg-neutral-800 active:scale-[0.99] rounded-2xl font-bold tracking-tight shadow-xl shadow-black/10 flex items-center justify-center gap-2 text-sm transition-all"
        >
          <Check className="w-4 h-4" />
          <span>Apply to Home Screen</span>
        </button>
      </div>
    </div>
  );
};
