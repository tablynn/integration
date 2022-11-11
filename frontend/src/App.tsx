import React from 'react';
import './App.css';
import MapBox from './MapBox';

/**
 * This class sets up the react app for the map.
 * @returns the created app
 */
function App(){
  return (
    <div className="App">
      <MapBox />
    </div>
  );
}

export default App;