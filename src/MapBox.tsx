import { useEffect, useState } from 'react';
import Map, { ViewState, ViewStateChangeEvent, MapLayerMouseEvent, Source, Layer } from 'react-map-gl'  


import { myKey } from './private/key'

import React from 'react';

export default function MapBox() {
  // Attributes of Mapbox
  const [viewState, setViewState] = useState<ViewState>({
    longitude: -111.9261,
    latitude: 33.5013,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  

  return (
    <div className="mapbox">

      <Map 
        mapboxAccessToken={myKey}
        latitude={viewState.latitude}
        longitude={viewState.longitude}
        zoom={viewState.zoom}
        pitch={viewState.pitch}
        bearing={viewState.bearing}
        padding={viewState.padding}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)} 
        onClick={(ev: MapLayerMouseEvent) => console.log(ev)}
        style={{width:window.innerWidth, height:window.innerHeight*0.9}} 
        mapStyle={'mapbox://styles/mapbox/light-v10'}>
      </Map>       
    </div>
  );
}

export{MapBox}