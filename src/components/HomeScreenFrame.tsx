import React from 'react';
import { ClockConfig, WidgetSize } from '../types';
import { ClockWidgetRenderer } from './ClockWidgetRenderer';
import { WallpaperOption } from '../data/constants';
import { Wifi, BatteryMedium, Signal, Search, Phone, MessageSquare, Camera, Settings } from 'lucide-react';

interface HomeScreenFrameProps {
  config: ClockConfig;
  size: WidgetSize;
  onSizeChange: (size: WidgetSize) => void;
  time: Date;
  wallpaper: WallpaperOption;
  onSelectWallpaper: (wp: WallpaperOption) => void;
  onWidgetClick?: () => void;
}

export const HomeScreenFrame: React.FC<HomeScreenFrameProps> = ({
  config,
  size,
  onSizeChange,
  time,
  wallpaper,
  onWidgetClick,
}) => {
  // Format current system time for the tiny status bar
  const statusTime = time.toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: !config.is24Hour,
  });

  // Calculate container height based on widget size
  const getWidgetDimensions = () => {
    switch (size) {
      case 'small':
        return 'w-[160px] h-[160px] sm:w-[170px] sm:h-[170px]';
      case 'medium':
        return 'w-[320px] h-[155px] sm:w-[350px] sm:h-[165px]';
      case 'large':
        return 'w-[320px] h-[320px] sm:w-[350px] sm:h-[350px]';
    }
  };

  return (
    <div className="flex flex-col items-center">
      {/* Size Selector Pill */}
      <div className="flex items-center gap-1.5 p-1 rounded-xl bg-gray-100 border border-gray-200 mb-4 shadow-sm">
        {(['small', 'medium', 'large'] as WidgetSize[]).map((s) => (
          <button
            key={s}
            id={`size-pill-${s}`}
            onClick={() => onSizeChange(s)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
              size === s
                ? 'bg-white text-gray-900 font-bold shadow-sm'
                : 'text-gray-500 hover:text-gray-800 hover:bg-white/50'
            }`}
          >
            {s === 'small' ? '2×2 Small' : s === 'medium' ? '4×2 Medium' : '4×4 Large'}
          </button>
        ))}
      </div>

      {/* Android Device Mockup Shell */}
      <div
        id="android-phone-frame"
        style={{ background: wallpaper.cssBackground }}
        className="relative w-[360px] sm:w-[390px] h-[720px] sm:h-[760px] rounded-[48px] border-[10px] border-neutral-900 shadow-2xl shadow-neutral-400/50 overflow-hidden flex flex-col justify-between p-4 transition-all duration-500"
      >
        {/* Subtle bezel ring highlight */}
        <div className="absolute inset-0 rounded-[38px] ring-1 ring-white/10 pointer-events-none" />

        {/* Top Camera Punch Hole & Status Bar */}
        <div className="relative z-20 pt-1">
          <div className="flex items-center justify-between text-xs text-white/80 font-medium px-2">
            <span className="tabular-nums tracking-tight">{statusTime}</span>

            {/* Centered Camera Punch Hole */}
            <div className="w-3.5 h-3.5 rounded-full bg-black ring-2 ring-neutral-800/80 mx-auto" />

            {/* Status Icons */}
            <div className="flex items-center gap-1.5 text-white/75">
              <Signal className="w-3.5 h-3.5" />
              <Wifi className="w-3.5 h-3.5" />
              <div className="flex items-center gap-1">
                <span className="text-[10px] tabular-nums font-mono">94%</span>
                <BatteryMedium className="w-4 h-4" />
              </div>
            </div>
          </div>
        </div>

        {/* Glance Header Bar / Date tag */}
        <div className="mt-4 px-3 flex items-center justify-between">
          <div className="text-white/60 text-[11px] font-medium tracking-wide">
            {time.toLocaleDateString('en-US', {
              weekday: 'short',
              month: 'short',
              day: 'numeric',
            })}
          </div>
          <div className="flex items-center gap-1 text-white/60 text-[11px]">
            <span>72°F</span>
            <span>· Sunny</span>
          </div>
        </div>

        {/* Home Screen Grid: Clock Widget Target Area */}
        <div className="my-auto flex flex-col items-center justify-center py-2 px-1">
          <div className={`transition-all duration-300 ${getWidgetDimensions()}`}>
            <ClockWidgetRenderer
              config={config}
              size={size}
              time={time}
              onClick={onWidgetClick}
              interactive={true}
            />
          </div>

          {/* Size hint badge */}
          <div className="mt-3 px-2 py-0.5 rounded-full bg-black/40 backdrop-blur-md border border-white/10 text-[10px] text-white/50 tracking-wider">
            {size === 'small' ? '2×2 Cell (110dp)' : size === 'medium' ? '4×2 Cell (240dp)' : '4×4 Cell (240dp)'}
          </div>
        </div>

        {/* Bottom Section: Search & App Dock */}
        <div className="space-y-4 pb-2 z-10">
          {/* Quick Search Bar */}
          <div className="h-11 rounded-full bg-white/15 backdrop-blur-md border border-white/20 px-4 flex items-center justify-between text-white/70 shadow-sm">
            <div className="flex items-center gap-2">
              <Search className="w-4 h-4 text-white/60" />
              <span className="text-xs text-white/50">Search apps, web...</span>
            </div>
            <div className="w-2 h-2 rounded-full bg-amber-400/80" />
          </div>

          {/* Home App Dock Icons */}
          <div className="flex items-center justify-around px-2 pt-1">
            <div className="w-11 h-11 rounded-2xl bg-blue-600/90 backdrop-blur-md flex items-center justify-center text-white shadow-md">
              <Phone className="w-5 h-5" />
            </div>
            <div className="w-11 h-11 rounded-2xl bg-emerald-600/90 backdrop-blur-md flex items-center justify-center text-white shadow-md">
              <MessageSquare className="w-5 h-5" />
            </div>
            <div className="w-11 h-11 rounded-2xl bg-neutral-800/90 border border-white/20 backdrop-blur-md flex items-center justify-center text-white shadow-md">
              <Camera className="w-5 h-5" />
            </div>
            <div className="w-11 h-11 rounded-2xl bg-amber-600/90 backdrop-blur-md flex items-center justify-center text-white shadow-md">
              <Settings className="w-5 h-5" />
            </div>
          </div>

          {/* Android Home Gesture Pill */}
          <div className="w-32 h-1 rounded-full bg-white/40 mx-auto" />
        </div>
      </div>
    </div>
  );
};
