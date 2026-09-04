import React from 'react';
import { ClockConfig, ClockStyleId, WidgetSize } from '../types';
import { CLOCK_STYLES, DEFAULT_CONFIGS } from '../data/constants';
import { ClockWidgetRenderer } from './ClockWidgetRenderer';
import { Sliders, Sparkles, Check, ArrowRight } from 'lucide-react';

interface StyleGalleryProps {
  currentConfig: ClockConfig;
  previewSize: WidgetSize;
  onSelectSize: (size: WidgetSize) => void;
  onSelectStyle: (styleId: ClockStyleId) => void;
  onApplyStyle: (styleId: ClockStyleId) => void;
  time: Date;
}

export const StyleGallery: React.FC<StyleGalleryProps> = ({
  currentConfig,
  previewSize,
  onSelectSize,
  onSelectStyle,
  onApplyStyle,
  time,
}) => {
  return (
    <div className="space-y-6">
      {/* Gallery Header & Size Filter Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-gray-200">
        <div>
          <div className="flex items-center gap-2">
            <h2 className="text-2xl font-bold tracking-tight text-gray-900">
              Clock Gallery
            </h2>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 font-bold font-mono border border-blue-200/70">
              6 Curated Styles
            </span>
          </div>
          <p className="text-xs text-gray-500 mt-1 max-w-xl">
            Choose your primary home screen widget style. Each layout features bespoke typography and spatial balance.
          </p>
        </div>

        {/* Global Preview Size Switcher */}
        <div className="flex items-center gap-1.5 p-1 rounded-xl bg-white border border-gray-200 shadow-sm self-start sm:self-auto">
          <span className="text-xs text-gray-400 px-2 font-medium">Size:</span>
          {(['small', 'medium', 'large'] as WidgetSize[]).map((s) => (
            <button
              key={s}
              id={`gallery-size-filter-${s}`}
              onClick={() => onSelectSize(s)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                previewSize === s
                  ? 'bg-gray-100 text-gray-900 font-bold shadow-sm'
                  : 'text-gray-500 hover:text-gray-900 hover:bg-gray-50'
              }`}
            >
              {s === 'small' ? '2×2 Small' : s === 'medium' ? '4×2 Medium' : '4×4 Large'}
            </button>
          ))}
        </div>
      </div>

      {/* Grid of 6 Distinct Styles - Bento Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {CLOCK_STYLES.map((styleDef) => {
          const isCurrent = currentConfig.style === styleDef.id;
          const styleConfig: ClockConfig = {
            ...DEFAULT_CONFIGS[styleDef.id],
            is24Hour: currentConfig.is24Hour,
            showSeconds: currentConfig.showSeconds,
          };

          // Define card container height depending on previewSize
          const containerHeight =
            previewSize === 'small'
              ? 'h-[175px]'
              : previewSize === 'medium'
              ? 'h-[175px]'
              : 'h-[260px]';

          return (
            <div
              key={styleDef.id}
              id={`style-card-${styleDef.id}`}
              className={`group rounded-[2rem] bg-white border transition-all duration-300 flex flex-col justify-between p-6 relative overflow-hidden shadow-sm hover:shadow-md ${
                isCurrent
                  ? 'border-blue-600 ring-2 ring-blue-500/20'
                  : 'border-gray-200 hover:border-gray-300'
              }`}
            >
              {/* Active Badge */}
              {isCurrent && (
                <div className="absolute top-5 right-5 z-20 flex items-center gap-1 text-[11px] font-bold bg-blue-100 text-blue-700 px-3 py-1 rounded-full shadow-sm">
                  <Check className="w-3 h-3 stroke-[3]" />
                  <span>ACTIVE</span>
                </div>
              )}

              {/* Title & Tagline */}
              <div className="mb-4 pr-20">
                <div className="flex items-center gap-2">
                  <h3 className="text-lg font-bold text-gray-900 tracking-tight">
                    {styleDef.name}
                  </h3>
                  <span className="text-[11px] text-gray-400 font-mono">
                    STYLE: {styleDef.id.toUpperCase()}
                  </span>
                </div>
                <p className="text-xs text-gray-400 mt-0.5 line-clamp-1">
                  {styleDef.tagline}
                </p>
              </div>

              {/* Live Preview Container on sleek dark surface */}
              <div
                className={`w-full ${containerHeight} rounded-2xl bg-[#121212] p-3 flex items-center justify-center border border-neutral-800/80 relative shadow-inner overflow-hidden`}
              >
                <div className="w-full h-full">
                  <ClockWidgetRenderer
                    config={styleConfig}
                    size={previewSize}
                    time={time}
                    interactive={false}
                  />
                </div>
              </div>

              {/* Description */}
              <p className="text-xs text-gray-500 mt-3.5 leading-relaxed min-h-[36px]">
                {styleDef.description}
              </p>

              {/* Action Buttons */}
              <div className="flex items-center gap-2 mt-4 pt-3 border-t border-gray-100">
                <button
                  id={`btn-customize-${styleDef.id}`}
                  onClick={() => onSelectStyle(styleDef.id)}
                  className="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-800 text-xs font-bold transition-colors"
                >
                  <Sliders className="w-3.5 h-3.5 text-gray-600" />
                  <span>Customize</span>
                </button>

                <button
                  id={`btn-apply-${styleDef.id}`}
                  onClick={() => onApplyStyle(styleDef.id)}
                  className={`py-2.5 px-4 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 shadow-sm ${
                    isCurrent
                      ? 'bg-blue-600 text-white shadow-blue-500/20'
                      : 'bg-black text-white hover:bg-neutral-800'
                  }`}
                >
                  <span>{isCurrent ? 'Applied' : 'Apply'}</span>
                  {!isCurrent && <ArrowRight className="w-3 h-3" />}
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
