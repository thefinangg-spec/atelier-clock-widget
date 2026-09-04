import React, { useState, useEffect } from 'react';
import { ClockConfig, ClockStyleId, WidgetSize } from './types';
import { DEFAULT_CONFIGS, WALLPAPERS, WallpaperOption } from './data/constants';
import { Header } from './components/Header';
import { HomeScreenFrame } from './components/HomeScreenFrame';
import { CustomizerPanel } from './components/CustomizerPanel';
import { StyleGallery } from './components/StyleGallery';
import { AndroidProjectExplorer } from './components/AndroidProjectExplorer';
import confetti from 'canvas-confetti';
import { Check, Sparkles, Smartphone, Download, Info } from 'lucide-react';

export default function App() {
  const [activeTab, setActiveTab] = useState<'studio' | 'gallery' | 'source'>('studio');
  const [config, setConfig] = useState<ClockConfig>(() => {
    const saved = localStorage.getItem('atelier_clock_config');
    if (saved) {
      try {
        return JSON.parse(saved);
      } catch (e) {
        // fallback
      }
    }
    return DEFAULT_CONFIGS.minimal;
  });

  const [size, setSize] = useState<WidgetSize>('medium');
  const [galleryPreviewSize, setGalleryPreviewSize] = useState<WidgetSize>('medium');
  const [wallpaper, setWallpaper] = useState<WallpaperOption>(WALLPAPERS[0]);
  const [currentTime, setCurrentTime] = useState<Date>(new Date());
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  // Live timer tick every 1000ms
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000);
    return () => clearInterval(interval);
  }, []);

  const showToast = (message: string) => {
    setToastMessage(message);
    setTimeout(() => {
      setToastMessage(null);
    }, 3200);
  };

  const handleSave = () => {
    localStorage.setItem('atelier_clock_config', JSON.stringify(config));
    confetti({
      particleCount: 40,
      spread: 60,
      origin: { y: 0.8 },
      colors: ['#F59E0B', '#10B981', '#3B82F6', '#F4F4F5'],
    });
    showToast('Configuration applied and saved for home screen!');
  };

  const handleResetDefaults = () => {
    const styleDefaults = DEFAULT_CONFIGS[config.style];
    setConfig(styleDefaults);
    showToast(`Reset to default ${config.style.toUpperCase()} style`);
  };

  const handleApplyStyleFromGallery = (styleId: ClockStyleId) => {
    const baseConfig = DEFAULT_CONFIGS[styleId];
    setConfig({
      ...baseConfig,
      is24Hour: config.is24Hour,
      showSeconds: config.showSeconds,
    });
    showToast(`Applied ${styleId.toUpperCase()} widget style!`);
  };

  const handleSelectStyleToCustomize = (styleId: ClockStyleId) => {
    handleApplyStyleFromGallery(styleId);
    setActiveTab('studio');
  };

  return (
    <div className="min-h-screen bg-[#F3F4F6] text-[#1A1A1A] flex flex-col font-sans selection:bg-blue-600/20 selection:text-blue-700">
      {/* Navigation Header */}
      <Header
        activeTab={activeTab}
        onTabChange={setActiveTab}
        selectedWallpaper={wallpaper}
        onSelectWallpaper={setWallpaper}
        currentTime={currentTime}
      />

      {/* Main Workspace Area */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 py-8">
        {/* Studio View: Side-by-side Phone Simulation and Customizer */}
        {activeTab === 'studio' && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            {/* Left: Interactive Phone Mockup */}
            <div className="lg:col-span-5 bg-white border border-gray-200 rounded-[2.5rem] p-6 sm:p-7 shadow-sm flex flex-col items-center">
              <div className="w-full flex flex-col items-center">
                <div className="mb-3 text-center">
                  <span className="text-xs font-bold uppercase tracking-wider text-gray-500">
                    Live Android Home Screen
                  </span>
                  <p className="text-[11px] text-gray-400">
                    Click widget or use size pills above to preview responsiveness
                  </p>
                </div>

                <HomeScreenFrame
                  config={config}
                  size={size}
                  onSizeChange={setSize}
                  time={currentTime}
                  wallpaper={wallpaper}
                  onSelectWallpaper={setWallpaper}
                  onWidgetClick={() => {
                    showToast('Glance tap action: Launches Clock or Customizer in Android');
                  }}
                />
              </div>
            </div>

            {/* Right: Customization Dashboard */}
            <div className="lg:col-span-7 bg-white border border-gray-200 rounded-[2.5rem] p-6 sm:p-8 shadow-sm">
              <CustomizerPanel
                config={config}
                onChange={setConfig}
                onResetDefaults={handleResetDefaults}
                onSave={handleSave}
              />
            </div>
          </div>
        )}

        {/* Gallery View: Compare all 6 visual styles */}
        {activeTab === 'gallery' && (
          <StyleGallery
            currentConfig={config}
            previewSize={galleryPreviewSize}
            onSelectSize={setGalleryPreviewSize}
            onSelectStyle={handleSelectStyleToCustomize}
            onApplyStyle={handleApplyStyleFromGallery}
            time={currentTime}
          />
        )}

        {/* Source & Android Project View */}
        {activeTab === 'source' && <AndroidProjectExplorer />}
      </main>

      {/* Floating Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-6 right-6 z-50 animate-bounce-short">
          <div className="flex items-center gap-2.5 px-4 py-3 rounded-2xl bg-white border border-gray-200 text-gray-900 shadow-xl shadow-black/10 text-xs font-semibold">
            <div className="w-5 h-5 rounded-full bg-blue-50 text-blue-600 flex items-center justify-center shrink-0">
              <Check className="w-3.5 h-3.5 stroke-[2.5]" />
            </div>
            <span>{toastMessage}</span>
          </div>
        </div>
      )}
    </div>
  );
}
