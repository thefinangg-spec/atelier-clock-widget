import React from 'react';
import { ClockConfig, WidgetSize } from '../types';
import { ACCENT_COLORS } from '../data/constants';

interface ClockWidgetRendererProps {
  config: ClockConfig;
  size: WidgetSize;
  time: Date;
  onClick?: () => void;
  interactive?: boolean;
}

export const ClockWidgetRenderer: React.FC<ClockWidgetRendererProps> = ({
  config,
  size,
  time,
  onClick,
  interactive = true,
}) => {
  const accent = ACCENT_COLORS.find((c) => c.id === config.accentColorId) || ACCENT_COLORS[0];

  // Time calculations
  const rawHours = time.getHours();
  const rawMinutes = time.getMinutes();
  const rawSeconds = time.getSeconds();

  const isPm = rawHours >= 12;
  const displayHours = config.is24Hour
    ? String(rawHours).padStart(2, '0')
    : String(rawHours % 12 === 0 ? 12 : rawHours % 12);
  const displayMinutes = String(rawMinutes).padStart(2, '0');
  const displaySeconds = String(rawSeconds).padStart(2, '0');
  const amPm = isPm ? 'PM' : 'AM';

  const weekdayShort = time.toLocaleDateString('en-US', { weekday: 'short' });
  const weekdayFull = time.toLocaleDateString('en-US', { weekday: 'long' });
  const monthShort = time.toLocaleDateString('en-US', { month: 'short' });
  const monthFull = time.toLocaleDateString('en-US', { month: 'long' });
  const dayNum = time.getDate();
  const yearNum = time.getFullYear();

  // Alignment classes
  const alignClass =
    config.alignment === 'center'
      ? 'items-center text-center justify-center'
      : config.alignment === 'right'
      ? 'items-end text-right justify-end'
      : 'items-start text-left justify-start';

  // Padding classes
  const paddingClass =
    config.padding === 'compact'
      ? 'p-3.5'
      : config.padding === 'generous'
      ? 'p-6'
      : 'p-4 sm:p-5';

  // Radius style
  const borderRadius = `${config.cornerRadius}px`;

  // Scale multiplier
  const scaleRatio =
    config.textSize === 'compact' ? 0.85 : config.textSize === 'large' ? 1.15 : 1.0;

  // Background style computation
  const opacityRatio = config.bgOpacity / 100;
  let bgStyleCss: React.CSSProperties = {};
  let borderClass = 'border border-white/10';

  if (config.bgStyle === 'solid') {
    bgStyleCss = {
      backgroundColor: `rgba(20, 20, 23, ${Math.max(0.4, opacityRatio)})`,
    };
  } else if (config.bgStyle === 'glass') {
    bgStyleCss = {
      background: `linear-gradient(135deg, rgba(42, 42, 48, ${opacityRatio}) 0%, rgba(26, 26, 30, ${opacityRatio}) 50%, rgba(18, 18, 22, ${Math.min(0.95, opacityRatio * 1.08)}) 100%)`,
      backdropFilter: 'blur(16px)',
      WebkitBackdropFilter: 'blur(16px)',
    };
    borderClass = 'border border-white/20 shadow-lg shadow-black/25';
  } else if (config.bgStyle === 'outline') {
    bgStyleCss = {
      backgroundColor: `rgba(10, 10, 12, ${opacityRatio * 0.5})`,
    };
    borderClass = 'border-2 border-white/30';
  } else if (config.bgStyle === 'transparent') {
    bgStyleCss = {
      backgroundColor: 'transparent',
    };
    borderClass = 'border border-transparent';
  } else if (config.bgStyle === 'mesh') {
    bgStyleCss = {
      backgroundImage: `radial-gradient(ellipse at 10% 10%, ${accent.hex}22 0%, rgba(24, 24, 27, ${opacityRatio}) 75%)`,
      backdropFilter: 'blur(12px)',
      WebkitBackdropFilter: 'blur(12px)',
    };
    borderClass = 'border border-white/15';
  }

  // Determine font family
  const getFontFamily = () => {
    switch (config.font) {
      case 'editorial-serif':
        return 'font-editorial';
      case 'digital-mono':
        return 'font-digital';
      case 'code-mono':
        return 'font-terminal';
      case 'expressive':
        return 'font-typographic';
      default:
        return 'font-minimal';
    }
  };

  const fontFamilyClass = getFontFamily();

  // Distinct Style Renderers
  const renderMinimal = () => {
    const timeSize =
      size === 'small'
        ? `${34 * scaleRatio}px`
        : size === 'medium'
        ? `${50 * scaleRatio}px`
        : `${64 * scaleRatio}px`;

    return (
      <div className={`flex flex-col h-full justify-between ${alignClass}`}>
        <div className="flex items-baseline gap-1.5 leading-none">
          <span
            style={{ fontSize: timeSize, color: accent.hex }}
            className={`${fontFamilyClass} font-light tracking-tight tabular-nums`}
          >
            {displayHours}:{displayMinutes}
          </span>
          {config.showSeconds && (
            <span
              style={{ fontSize: `${16 * scaleRatio}px` }}
              className="text-white/45 font-light tabular-nums"
            >
              :{displaySeconds}
            </span>
          )}
          {!config.is24Hour && (
            <span
              style={{ fontSize: `${11 * scaleRatio}px` }}
              className="text-white/40 tracking-wider uppercase font-medium ml-0.5"
            >
              {amPm}
            </span>
          )}
        </div>

        {(config.showWeekday || config.showDate) && (
          <div className="flex items-center gap-2 pt-1 border-t border-white/10 w-full">
            {config.showWeekday && (
              <span className="text-[11px] font-medium tracking-wider uppercase text-white/70">
                {weekdayShort}
              </span>
            )}
            {config.showWeekday && config.showDate && (
              <span className="text-white/20 text-xs">/</span>
            )}
            {config.showDate && (
              <span className="text-[11px] font-normal tracking-wide text-white/50">
                {monthShort} {dayNum}
              </span>
            )}
          </div>
        )}
      </div>
    );
  };

  const renderEditorial = () => {
    if (size === 'small') {
      return (
        <div className="flex flex-col h-full justify-between">
          <div className="leading-none">
            <div
              style={{ fontSize: `${42 * scaleRatio}px`, color: accent.hex }}
              className="font-editorial italic font-bold tracking-tight"
            >
              {displayHours}
            </div>
            <div
              style={{ fontSize: `${42 * scaleRatio}px` }}
              className="font-editorial text-white/80 font-normal -mt-1 tracking-tight"
            >
              {displayMinutes}
            </div>
          </div>
          {config.showDate && (
            <div className="text-[10px] tracking-widest uppercase text-white/50 font-sans border-t border-white/10 pt-1.5">
              {weekdayShort} · {monthShort} {dayNum}
            </div>
          )}
        </div>
      );
    }

    if (size === 'medium') {
      return (
        <div className="flex items-center justify-between h-full w-full">
          <div className="flex items-baseline gap-1">
            <span
              style={{ fontSize: `${54 * scaleRatio}px`, color: accent.hex }}
              className="font-editorial font-bold italic tracking-tight"
            >
              {displayHours}:{displayMinutes}
            </span>
            {config.showSeconds && (
              <span className="text-white/40 font-editorial text-xl italic">
                .{displaySeconds}
              </span>
            )}
          </div>
          <div className="flex flex-col text-right pl-3 border-l border-white/10">
            {config.showWeekday && (
              <span className="text-xs font-sans font-semibold tracking-widest uppercase text-white/90">
                {weekdayFull}
              </span>
            )}
            {config.showDate && (
              <span
                style={{ color: accent.hex }}
                className="font-editorial text-sm italic text-white/70"
              >
                {monthFull} {dayNum}
              </span>
            )}
            {!config.is24Hour && (
              <span className="text-[10px] font-sans tracking-widest uppercase text-white/35 mt-0.5">
                {amPm} EDITION
              </span>
            )}
          </div>
        </div>
      );
    }

    // Large 4x4
    return (
      <div className="flex flex-col justify-between h-full w-full">
        <div className="flex justify-between items-start border-b border-white/15 pb-2">
          <span className="text-[11px] font-sans font-bold tracking-[0.2em] uppercase text-white/50">
            CHRONICLE · VOL. {yearNum}
          </span>
          <span
            style={{ color: accent.hex }}
            className="text-[11px] font-sans font-semibold tracking-widest uppercase"
          >
            {weekdayFull}
          </span>
        </div>
        <div className="py-2">
          <div
            style={{ fontSize: `${72 * scaleRatio}px`, color: accent.hex }}
            className="font-editorial font-bold italic leading-none tracking-tight"
          >
            {displayHours}:{displayMinutes}
          </div>
          {config.showSeconds && (
            <div className="text-white/40 font-editorial text-lg italic mt-1">
              Seconds passing: {displaySeconds}s
            </div>
          )}
        </div>
        <div className="flex justify-between items-end border-t border-white/10 pt-2">
          <div className="text-xs font-editorial italic text-white/60">
            {monthFull} {dayNum}, {yearNum}
          </div>
          {!config.is24Hour && (
            <div className="text-[11px] font-sans font-medium uppercase tracking-widest text-white/40">
              {amPm} Standard
            </div>
          )}
        </div>
      </div>
    );
  };

  const renderDigital = () => {
    const timeSize =
      size === 'small'
        ? `${28 * scaleRatio}px`
        : size === 'medium'
        ? `${42 * scaleRatio}px`
        : `${52 * scaleRatio}px`;

    return (
      <div className={`flex flex-col h-full justify-between ${alignClass}`}>
        <div className="flex items-center justify-between w-full text-[10px] font-digital font-medium text-white/40 tracking-wider">
          <span className="flex items-center gap-1">
            <span
              className="inline-block w-1.5 h-1.5 rounded-full animate-pulse"
              style={{ backgroundColor: accent.hex }}
            />
            ACTIVE
          </span>
          <span>{config.is24Hour ? '24HR' : amPm}</span>
        </div>

        <div className="flex items-baseline gap-1 my-auto">
          <span
            style={{ fontSize: timeSize, color: accent.hex }}
            className="font-digital font-bold tracking-wider tabular-nums drop-shadow-sm"
          >
            {displayHours}
            <span className="animate-pulse opacity-80">:</span>
            {displayMinutes}
          </span>
          {config.showSeconds && (
            <span
              style={{ fontSize: `${15 * scaleRatio}px` }}
              className="font-digital text-white/50 font-medium tabular-nums ml-1 px-1.5 py-0.5 rounded bg-white/5"
            >
              {displaySeconds}
            </span>
          )}
        </div>

        {(config.showWeekday || config.showDate) && (
          <div className="w-full flex items-center justify-between text-[11px] font-digital text-white/50 pt-1.5 border-t border-white/10">
            {config.showWeekday && (
              <span className="uppercase tracking-widest text-white/80">{weekdayShort}</span>
            )}
            {config.showDate && (
              <span className="tabular-nums tracking-wider">
                [{yearNum}.{String(time.getMonth() + 1).padStart(2, '0')}.
                {String(dayNum).padStart(2, '0')}]
              </span>
            )}
          </div>
        )}
      </div>
    );
  };

  const renderTerminal = () => {
    const timeSize =
      size === 'small'
        ? `${20 * scaleRatio}px`
        : size === 'medium'
        ? `${30 * scaleRatio}px`
        : `${36 * scaleRatio}px`;

    return (
      <div className="flex flex-col h-full justify-between font-terminal text-left">
        <div className="flex items-center justify-between text-[10px] text-white/40 border-b border-white/10 pb-1">
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-emerald-500/80 inline-block" />
            <span>sys.clock</span>
          </span>
          <span className="text-[9px] text-white/30">glance_v1.1</span>
        </div>

        <div className="py-1">
          <div className="text-[11px] text-white/40 leading-tight">
            <span style={{ color: accent.hex }}>$</span> read --current
          </div>
          <div
            style={{ fontSize: timeSize, color: accent.hex }}
            className="font-bold tracking-normal leading-tight tabular-nums mt-0.5 flex items-baseline gap-1"
          >
            <span>&gt;</span>
            <span>
              {displayHours}:{displayMinutes}
            </span>
            {config.showSeconds && (
              <span className="text-white/40 text-xs">:{displaySeconds}</span>
            )}
            {!config.is24Hour && (
              <span className="text-[10px] text-white/40 font-normal uppercase ml-1">
                {amPm}
              </span>
            )}
            <span className="w-2 h-4 bg-white/70 inline-block animate-pulse ml-0.5" />
          </div>
        </div>

        {(config.showWeekday || config.showDate) && (
          <div className="text-[10px] text-white/50 border-t border-white/10 pt-1 space-y-0.5 leading-tight">
            {config.showWeekday && (
              <div>
                <span className="text-white/30">day: </span>
                <span className="text-white/80">{weekdayFull.toLowerCase()}</span>
              </div>
            )}
            {config.showDate && (
              <div>
                <span className="text-white/30">iso: </span>
                <span className="text-white/80">
                  {yearNum}-{String(time.getMonth() + 1).padStart(2, '0')}-
                  {String(dayNum).padStart(2, '0')}
                </span>
              </div>
            )}
          </div>
        )}
      </div>
    );
  };

  const renderTypographic = () => {
    if (size === 'small') {
      return (
        <div className="flex flex-col justify-between h-full">
          <div className="leading-none">
            <div
              style={{ fontSize: `${44 * scaleRatio}px`, color: accent.hex }}
              className="font-typographic font-extrabold tracking-tighter"
            >
              {displayHours}
            </div>
            <div
              style={{ fontSize: `${44 * scaleRatio}px` }}
              className="font-typographic font-bold text-white/60 -mt-2 tracking-tighter"
            >
              {displayMinutes}
            </div>
          </div>
          {config.showDate && (
            <div
              style={{ backgroundColor: `${accent.hex}25`, color: accent.hex }}
              className="text-[11px] font-typographic font-bold px-2 py-0.5 rounded-full inline-block self-start"
            >
              {dayNum} {monthShort.toUpperCase()}
            </div>
          )}
        </div>
      );
    }

    if (size === 'medium') {
      return (
        <div className="flex items-center justify-between h-full w-full">
          <div className="flex items-baseline tracking-tighter">
            <span
              style={{ fontSize: `${58 * scaleRatio}px`, color: accent.hex }}
              className="font-typographic font-extrabold leading-none"
            >
              {displayHours}
            </span>
            <span
              style={{ fontSize: `${44 * scaleRatio}px` }}
              className="font-typographic font-bold text-white/40 leading-none mx-0.5"
            >
              :
            </span>
            <span
              style={{ fontSize: `${58 * scaleRatio}px` }}
              className="font-typographic font-bold text-white/80 leading-none"
            >
              {displayMinutes}
            </span>
          </div>

          <div className="flex flex-col items-end gap-1">
            <div
              style={{ backgroundColor: `${accent.hex}22`, borderColor: `${accent.hex}44` }}
              className="border px-2.5 py-1 rounded-full text-right"
            >
              <span
                style={{ color: accent.hex }}
                className="text-xs font-typographic font-extrabold tracking-wide uppercase"
              >
                {weekdayShort} {dayNum}
              </span>
            </div>
            {config.showSeconds && (
              <span className="text-[11px] font-typographic font-medium text-white/40 tracking-wider">
                {displaySeconds} SEC
              </span>
            )}
          </div>
        </div>
      );
    }

    // Large 4x4
    return (
      <div className="flex flex-col justify-between h-full w-full">
        <div className="flex justify-between items-start">
          <span
            style={{ color: accent.hex }}
            className="text-xs font-typographic font-extrabold tracking-widest uppercase"
          >
            {weekdayFull}
          </span>
          {!config.is24Hour && (
            <span className="text-[11px] font-typographic font-bold px-2 py-0.5 rounded-full bg-white/10 text-white/70">
              {amPm}
            </span>
          )}
        </div>

        <div className="py-2">
          <div
            style={{ fontSize: `${76 * scaleRatio}px`, color: accent.hex }}
            className="font-typographic font-black tracking-tighter leading-none"
          >
            {displayHours}
          </div>
          <div
            style={{ fontSize: `${76 * scaleRatio}px` }}
            className="font-typographic font-bold text-white/70 tracking-tighter leading-none -mt-3"
          >
            {displayMinutes}
          </div>
        </div>

        <div className="flex items-center justify-between border-t border-white/15 pt-2">
          <span className="text-xs font-typographic font-medium text-white/50 tracking-wider">
            {monthFull} {dayNum}, {yearNum}
          </span>
          {config.showSeconds && (
            <span
              style={{ color: accent.hex }}
              className="text-xs font-typographic font-bold tracking-widest"
            >
              .{displaySeconds}
            </span>
          )}
        </div>
      </div>
    );
  };

  const renderGlass = () => {
    const timeSize =
      size === 'small'
        ? `${32 * scaleRatio}px`
        : size === 'medium'
        ? `${46 * scaleRatio}px`
        : `${58 * scaleRatio}px`;

    return (
      <>
        {/* Full-frame Glass Specular & Ambient Glow Gradients fitting frame */}
        <div
          className="absolute inset-0 pointer-events-none overflow-hidden rounded-[inherit]"
          aria-hidden="true"
        >
          {/* Luminous ambient accent gradient fitting the frame from top-right corner */}
          <div
            className="absolute inset-0 opacity-45 transition-opacity duration-300"
            style={{
              background: `radial-gradient(ellipse 95% 85% at 100% 0%, ${accent.hex} 0%, ${accent.hex}33 35%, transparent 70%)`,
            }}
          />
          {/* Diagonal frosted glass specular sheen fitting corner-to-corner */}
          <div
            className="absolute inset-0"
            style={{
              background:
                'linear-gradient(135deg, rgba(255, 255, 255, 0.18) 0%, rgba(255, 255, 255, 0.03) 40%, transparent 60%, rgba(255, 255, 255, 0.07) 100%)',
            }}
          />
          {/* Top specular contour highlight fitting the top edge */}
          <div
            className="absolute top-0 inset-x-0 h-[1px]"
            style={{
              background:
                'linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.5) 30%, rgba(255, 255, 255, 0.25) 70%, transparent 100%)',
            }}
          />
          {/* Bottom subtle ambient bounce glow */}
          <div
            className="absolute bottom-0 inset-x-0 h-1/3 opacity-25"
            style={{
              background: `radial-gradient(ellipse at 50% 100%, ${accent.hex} 0%, transparent 80%)`,
            }}
          />
        </div>

        {/* Content Container (sitting cleanly on top of the frame-fitted glass gradient) */}
        <div className={`flex flex-col h-full justify-between ${alignClass} relative z-10 w-full`}>
          <div className="w-full flex items-center justify-between text-[10px] text-white/60 relative z-10">
            <span className="font-medium tracking-wider flex items-center gap-1.5">
              <span
                className="w-1.5 h-1.5 rounded-full"
                style={{ backgroundColor: accent.hex, boxShadow: `0 0 8px ${accent.hex}` }}
              />
              {config.showWeekday ? weekdayShort.toUpperCase() : 'TIME'}
            </span>
            {!config.is24Hour && (
              <span className="px-1.5 py-0.5 rounded-md bg-white/10 text-white/80 text-[9px] font-semibold border border-white/10">
                {amPm}
              </span>
            )}
          </div>

          <div className="flex items-baseline gap-1 my-auto relative z-10">
            <span
              style={{ fontSize: timeSize, color: accent.hex }}
              className="font-glass font-light tracking-tight tabular-nums drop-shadow-[0_2px_12px_rgba(0,0,0,0.5)]"
            >
              {displayHours}:{displayMinutes}
            </span>
            {config.showSeconds && (
              <span
                style={{ fontSize: `${15 * scaleRatio}px` }}
                className="text-white/50 font-light tabular-nums"
              >
                :{displaySeconds}
              </span>
            )}
          </div>

          {config.showDate && (
            <div className="w-full text-[11px] text-white/70 font-light flex items-center justify-between pt-1 border-t border-white/10 relative z-10">
              <span>
                {monthShort} {dayNum}
              </span>
              <span className="text-[10px] text-white/40 tracking-widest">{yearNum}</span>
            </div>
          )}
        </div>
      </>
    );
  };

  const renderStyleContent = () => {
    switch (config.style) {
      case 'minimal':
        return renderMinimal();
      case 'editorial':
        return renderEditorial();
      case 'digital':
        return renderDigital();
      case 'terminal':
        return renderTerminal();
      case 'typographic':
        return renderTypographic();
      case 'glass':
        return renderGlass();
      default:
        return renderMinimal();
    }
  };

  return (
    <div
      id={`clock-widget-${config.style}-${size}`}
      onClick={onClick}
      style={{
        borderRadius,
        ...bgStyleCss,
      }}
      className={`relative select-none transition-all duration-200 ${paddingClass} ${borderClass} overflow-hidden ${
        interactive ? 'cursor-pointer hover:border-white/30 active:scale-[0.99]' : ''
      } w-full h-full flex flex-col`}
    >
      {/* Top specular highlight contour for any glass background */}
      {config.bgStyle === 'glass' && config.style !== 'glass' && (
        <div
          className="absolute top-0 inset-x-0 h-[1px] pointer-events-none z-10"
          style={{
            background:
              'linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.4) 30%, rgba(255, 255, 255, 0.15) 70%, transparent 100%)',
          }}
        />
      )}
      {renderStyleContent()}
    </div>
  );
};
