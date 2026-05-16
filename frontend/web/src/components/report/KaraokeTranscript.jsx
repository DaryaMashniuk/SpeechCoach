import React, { useState, useEffect } from 'react';

const KaraokeTranscript = ({ segments, currentTime, onWordClick }) => {
    return (
        <div className="bg-white p-8 rounded-3xl shadow-inner border border-gray-100 leading-relaxed text-lg h-96 overflow-y-auto custom-scrollbar">
            <div className="flex flex-wrap gap-x-2 gap-y-3">
                {segments.map((seg, idx) => {
                    const isActive = currentTime >= (seg.start / 1000) && currentTime <= (seg.end / 1000);
                    return (
                        <span
                            key={idx}
                            onClick={() => onWordClick(seg.start / 1000)}
                            className={`cursor-pointer transition-all duration-300 px-1 rounded-md ${
                                isActive 
                                ? 'bg-yellow-300 text-black scale-110 shadow-sm font-bold' 
                                : 'text-gray-600 hover:bg-gray-100'
                            }`}
                        >
                            {seg.text}
                        </span>
                    );
                })}
            </div>
        </div>
    );
};

export default KaraokeTranscript;