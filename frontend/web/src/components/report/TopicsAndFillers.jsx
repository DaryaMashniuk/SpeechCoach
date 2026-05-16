import React, { useState } from 'react';

const TopicsAndFillers = ({ detectedTopics, fillerWords, fillerCount, t }) => {
  const [showAllTopics, setShowAllTopics] = useState(false);
  const [showAllFillers, setShowAllFillers] = useState(false);
  
  const displayTopics = showAllTopics ? detectedTopics : detectedTopics?.slice(0, 4);
  const displayFillers = showAllFillers ? fillerWords : fillerWords?.slice(0, 8);
  
  return (
    <div className="topics-fillers-section">
      {/* Обнаруженные темы */}
      {detectedTopics && detectedTopics.length > 0 && (
        <div className="topics-card">
          <div className="card-header">
            <h3>🔍 {t.report.detectedTopics || 'Обнаруженные темы'}</h3>
            {detectedTopics.length > 4 && (
              <button 
                className="show-more-btn"
                onClick={() => setShowAllTopics(!showAllTopics)}
              >
                {showAllTopics ? '📖 Свернуть' : `📖 Показать все (${detectedTopics.length})`}
              </button>
            )}
          </div>
          <div className="topics-cloud">
            {displayTopics?.map((topic, idx) => (
              <span key={idx} className="topic-tag">
                {topic}
              </span>
            ))}
          </div>
        </div>
      )}
      
      {/* Слова-паразиты */}
      {fillerWords && fillerWords.length > 0 && (
        <div className="fillers-card">
          <div className="card-header">
            <h3>🔤 {t.report.fillers || 'Слова-паразиты'}</h3>
            <div className="filler-stats">
              <span className="filler-count">Всего: {fillerCount || fillerWords.length}</span>
              {fillerWords.length > 8 && (
                <button 
                  className="show-more-btn"
                  onClick={() => setShowAllFillers(!showAllFillers)}
                >
                  {showAllFillers ? 'Свернуть' : `Показать все (${fillerWords.length})`}
                </button>
              )}
            </div>
          </div>
          <div className="fillers-cloud">
            {displayFillers?.map((word, idx) => (
              <span key={idx} className="filler-tag" style={{
                fontSize: `${12 + Math.min(8, fillerWords.filter(w => w === word).length * 2)}px`,
                opacity: 0.6 + Math.min(0.4, fillerWords.filter(w => w === word).length * 0.1)
              }}>
                {word}
              </span>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default TopicsAndFillers;