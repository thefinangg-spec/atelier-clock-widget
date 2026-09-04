import React, { useState } from 'react';
import { ANDROID_PROJECT_FILES, AndroidFile } from '../data/androidProjectFiles';
import JSZip from 'jszip';
import {
  Download,
  Copy,
  Check,
  FileCode,
  FolderTree,
  Cpu,
  BatteryCharging,
  ShieldCheck,
  Layers,
  Sparkles,
  ExternalLink,
} from 'lucide-react';

export const AndroidProjectExplorer: React.FC = () => {
  const [selectedFile, setSelectedFile] = useState<AndroidFile>(ANDROID_PROJECT_FILES[0]);
  const [copied, setCopied] = useState(false);
  const [isZipping, setIsZipping] = useState(false);
  const [downloadSuccess, setDownloadSuccess] = useState(false);

  const handleCopy = () => {
    navigator.clipboard.writeText(selectedFile.content);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const handleDownloadZip = async () => {
    setIsZipping(true);
    try {
      const zip = new JSZip();
      const rootFolder = zip.folder('AtelierClock');

      if (rootFolder) {
        ANDROID_PROJECT_FILES.forEach((file) => {
          rootFolder.file(file.path, file.content);
        });

        const blob = await zip.generateAsync({ type: 'blob' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'atelier-clock-android-studio-project.zip';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
      }

      setDownloadSuccess(true);
      setTimeout(() => setDownloadSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to generate zip:', err);
    } finally {
      setIsZipping(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header & Download Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-6 rounded-[2rem] bg-white border border-gray-200 shadow-sm">
        <div>
          <div className="flex items-center gap-2">
            <span className="w-2.5 h-2.5 rounded-full bg-blue-600" />
            <h2 className="text-xl font-bold text-gray-900 tracking-tight">
              Android Studio Project Source & Architecture
            </h2>
            <span className="text-[11px] font-mono px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 font-bold border border-blue-200/70">
              Kotlin 2.0 · Glance 1.1.1
            </span>
          </div>
          <p className="text-xs text-gray-500 mt-1 max-w-xl">
            Complete, runnable Android Studio project ready to compile with Gradle. Fully adheres to Android widget lifecycle constraints.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            id="btn-download-project-zip"
            onClick={handleDownloadZip}
            disabled={isZipping}
            className="flex items-center gap-2 py-3 px-5 rounded-2xl bg-black hover:bg-neutral-800 active:scale-[0.98] text-white font-bold text-xs transition-all shadow-xl shadow-black/10 disabled:opacity-50"
          >
            {downloadSuccess ? (
              <>
                <Check className="w-4 h-4 text-emerald-400 stroke-[3]" />
                <span>Downloaded!</span>
              </>
            ) : isZipping ? (
              <>
                <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                <span>Bundling ZIP...</span>
              </>
            ) : (
              <>
                <Download className="w-4 h-4 text-white" />
                <span>Download Android Project (.ZIP)</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* Technical Highlights Architecture Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="p-5 rounded-[1.75rem] bg-white border border-gray-200 shadow-sm flex items-start gap-3.5">
          <div className="p-2.5 rounded-2xl bg-blue-50 text-blue-600 shrink-0">
            <Cpu className="w-5 h-5" />
          </div>
          <div>
            <h4 className="text-xs font-bold text-gray-900">AndroidX Glance DSL</h4>
            <p className="text-[11px] text-gray-500 mt-1 leading-relaxed">
              Uses declarative Jetpack Compose Glance DSL (<code className="text-gray-800 bg-gray-100 px-1 py-0.5 rounded">GlanceAppWidget</code>) compiling directly into launcher-safe RemoteViews.
            </p>
          </div>
        </div>

        <div className="p-5 rounded-[1.75rem] bg-white border border-gray-200 shadow-sm flex items-start gap-3.5">
          <div className="p-2.5 rounded-2xl bg-emerald-50 text-emerald-600 shrink-0">
            <BatteryCharging className="w-5 h-5" />
          </div>
          <div>
            <h4 className="text-xs font-bold text-gray-900">Battery & Lifecycle Zero-Drain</h4>
            <p className="text-[11px] text-gray-500 mt-1 leading-relaxed">
              Updates through WorkManager with deduplication (<code className="text-gray-800 bg-gray-100 px-1 py-0.5 rounded">ExistingPeriodicWorkPolicy.KEEP</code>) + boot & timezone broadcast receivers.
            </p>
          </div>
        </div>

        <div className="p-5 rounded-[1.75rem] bg-white border border-gray-200 shadow-sm flex items-start gap-3.5">
          <div className="p-2.5 rounded-2xl bg-indigo-50 text-indigo-600 shrink-0">
            <ShieldCheck className="w-5 h-5" />
          </div>
          <div>
            <h4 className="text-xs font-bold text-gray-900">Reboot & Timezone Safe</h4>
            <p className="text-[11px] text-gray-500 mt-1 leading-relaxed">
              Responds immediately to <code className="text-gray-800 bg-gray-100 px-1 py-0.5 rounded">TIMEZONE_CHANGED</code> and system boot, preventing stale time after flights or restarts.
            </p>
          </div>
        </div>
      </div>

      {/* Main File Browser & Code Viewer */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-0 rounded-[2rem] bg-white border border-gray-200 shadow-sm overflow-hidden min-h-[560px]">
        {/* Left Sidebar: File Tree */}
        <div className="lg:col-span-4 border-b lg:border-b-0 lg:border-r border-gray-200 p-5 space-y-3 bg-gray-50/50">
          <div className="flex items-center gap-2 text-xs font-bold text-gray-800 pb-2 border-b border-gray-200/80">
            <FolderTree className="w-4 h-4 text-blue-600" />
            <span>Project Files ({ANDROID_PROJECT_FILES.length})</span>
          </div>

          <div className="space-y-1">
            {ANDROID_PROJECT_FILES.map((file) => {
              const isSelected = selectedFile.path === file.path;
              return (
                <button
                  key={file.path}
                  id={`file-tree-${file.name}`}
                  onClick={() => setSelectedFile(file)}
                  className={`w-full text-left p-2.5 rounded-xl text-xs transition-all flex items-start gap-2.5 ${
                    isSelected
                      ? 'bg-white border border-blue-500/50 text-blue-900 shadow-sm font-semibold'
                      : 'hover:bg-white text-gray-600 hover:text-gray-900 border border-transparent'
                  }`}
                >
                  <FileCode
                    className={`w-4 h-4 mt-0.5 shrink-0 ${
                      isSelected ? 'text-blue-600' : 'text-gray-400'
                    }`}
                  />
                  <div className="overflow-hidden">
                    <div className="font-medium truncate">{file.name}</div>
                    <div className="text-[10px] text-gray-400 font-mono truncate">
                      {file.path.split('/').slice(0, -1).join('/')}
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        </div>

        {/* Right Code Display */}
        <div className="lg:col-span-8 flex flex-col justify-between bg-white">
          {/* File Header */}
          <div className="flex items-center justify-between p-4 border-b border-gray-200 bg-white">
            <div>
              <div className="flex items-center gap-2">
                <span className="font-mono text-xs font-bold text-gray-900">
                  {selectedFile.path}
                </span>
                <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-md bg-gray-100 text-gray-600 font-semibold">
                  {selectedFile.language}
                </span>
              </div>
              <p className="text-xs text-gray-400 mt-1">{selectedFile.description}</p>
            </div>

            <button
              id="btn-copy-source"
              onClick={handleCopy}
              className="flex items-center gap-1.5 py-1.5 px-3 rounded-xl bg-gray-100 hover:bg-gray-200 text-gray-700 text-xs font-semibold transition-colors"
            >
              {copied ? (
                <>
                  <Check className="w-3.5 h-3.5 text-emerald-600 stroke-[3]" />
                  <span className="text-emerald-600">Copied</span>
                </>
              ) : (
                <>
                  <Copy className="w-3.5 h-3.5" />
                  <span>Copy Code</span>
                </>
              )}
            </button>
          </div>

          {/* Syntax Highlighted Code Viewer */}
          <div className="p-5 bg-[#121212] flex-1 overflow-x-auto font-mono text-xs leading-relaxed text-neutral-300 max-h-[500px] overflow-y-auto">
            <pre className="space-y-0.5">
              {selectedFile.content.split('\n').map((line, idx) => (
                <div key={idx} className="table-row hover:bg-neutral-800/40">
                  <span className="table-cell pr-4 text-right select-none text-neutral-600 w-10">
                    {idx + 1}
                  </span>
                  <span className="table-cell whitespace-pre">{line}</span>
                </div>
              ))}
            </pre>
          </div>

          {/* Footer status */}
          <div className="p-3 border-t border-neutral-800 bg-[#0d0d0d] text-[11px] text-neutral-400 flex items-center justify-between">
            <span>{selectedFile.content.split('\n').length} lines · UTF-8</span>
            <span>Target SDK 35 (Android 15)</span>
          </div>
        </div>
      </div>
    </div>
  );
};
