import React from 'react';
import { WallpaperOption, WALLPAPERS } from '../data/constants';
import { Clock, Smartphone, Grid, Code, Image as ImageIcon, Sparkles } from 'lucide-react';

interface HeaderProps {
  activeTab: 'studio' | 'gallery' | 'source';
  onTabChange: (tab: 'studio' | 'gallery' | 'source') => void;
  selectedWallpaper: WallpaperOption;
  onSelectWallpaper: (wp: WallpaperOption) => void;
  currentTime: Date;
}

export const Header: React.FC<HeaderProps> = ({
  activeTab,
  onTabChange,
  selectedWallpaper,
  onSelectWallpaper,
  currentTime,
}) => {
  const formattedTime = currentTime.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  });

  return (
    <header className="border-b border-gray-200 bg-white/90 backdrop-blur-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 h-16 flex items-center justify-between gap-4">
        {/* Logo & Subtitle */}
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-2xl bg-black flex items-center justify-center text-white font-bold shadow-sm">
            <Clock className="w-5 h-5 stroke-[2.5]" />
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-base font-bold text-gray-900 tracking-tight">
                Atelier Clock
              </h1>
              <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-full bg-blue-50 text-blue-600 font-bold border border-blue-200/80">
                Glance
              </span>
            </div>
            <p className="text-[11px] text-gray-500 hidden sm:block">
              Android Home-Screen Widget Studio
            </p>
          </div>
        </div>

        {/* Center Tabs */}
        <nav className="flex items-center gap-1 p-1 rounded-xl bg-gray-100 border border-gray-200/80">
          <button
            id="nav-tab-studio"
            onClick={() => onTabChange('studio')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-medium transition-all ${
              activeTab === 'studio'
                ? 'bg-white text-gray-900 font-bold shadow-sm'
                : 'text-gray-500 hover:text-gray-900 hover:bg-white/50'
            }`}
          >
            <Smartphone className={`w-3.5 h-3.5 ${activeTab === 'studio' ? 'text-blue-600' : ''}`} />
            <span>Studio</span>
          </button>

          <button
            id="nav-tab-gallery"
            onClick={() => onTabChange('gallery')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-medium transition-all ${
              activeTab === 'gallery'
                ? 'bg-white text-gray-900 font-bold shadow-sm'
                : 'text-gray-500 hover:text-gray-900 hover:bg-white/50'
            }`}
          >
            <Grid className={`w-3.5 h-3.5 ${activeTab === 'gallery' ? 'text-blue-600' : ''}`} />
            <span>Gallery</span>
          </button>

          <button
            id="nav-tab-source"
            onClick={() => onTabChange('source')}
            className={`flex items-center gap-1.5 px-3.5 py-1.5 rounded-lg text-xs font-medium transition-all ${
              activeTab === 'source'
                ? 'bg-white text-gray-900 font-bold shadow-sm'
                : 'text-gray-500 hover:text-gray-900 hover:bg-white/50'
            }`}
          >
            <Code className={`w-3.5 h-3.5 ${activeTab === 'source' ? 'text-blue-600' : ''}`} />
            <span className="hidden sm:inline">Android Studio Project</span>
            <span className="sm:hidden">Project</span>
          </button>
        </nav>

        {/* Right Tools: Live Clock Badge & Wallpaper Selector */}
        <div className="flex items-center gap-3">
          {/* Wallpaper quick picker */}
          <div className="hidden md:flex items-center gap-1 bg-white border border-gray-200 rounded-xl p-1 shadow-sm">
            <span className="text-[10px] text-gray-500 px-2 flex items-center gap-1">
              <ImageIcon className="w-3 h-3 text-gray-400" />
              <span>Wallpaper:</span>
            </span>
            {WALLPAPERS.map((wp) => (
              <button
                key={wp.id}
                id={`wp-header-${wp.id}`}
                onClick={() => onSelectWallpaper(wp)}
                title={wp.name}
                className={`w-5 h-5 rounded-full border transition-all ${
                  selectedWallpaper.id === wp.id
                    ? 'border-blue-600 ring-2 ring-blue-500/40 scale-110'
                    : 'border-black/10 hover:border-black/30'
                }`}
                style={{ background: wp.cssBackground }}
              />
            ))}
          </div>

          {/* Real Live Time Counter */}
          <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-white border border-gray-200 shadow-sm text-xs font-mono text-gray-800">
            <span className="w-2 h-2 rounded-full bg-blue-600 animate-pulse" />
            <span>{formattedTime}</span>
          </div>
        </div>
      </div>
    </header>
  );
};
