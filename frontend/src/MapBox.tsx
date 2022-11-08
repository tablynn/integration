import { useEffect, useState } from 'react';
import Map, { ViewState, ViewStateChangeEvent, MapLayerMouseEvent, Source, Layer } from 'react-map-gl'  


import { myKey } from './private/key'
import {overlayData, geoLayer} from './overlays' 
 

export default function MapBox() {
  // Attributes of Mapbox
  const [viewState, setViewState] = useState<ViewState>({
    longitude: -71.4128,
    latitude: 41.8240,
    zoom: 10,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  

  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

    // Run this _once_, and never refresh (empty dependency list)
    useEffect(() => {
      setOverlay(overlayData)
    }, [])

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
          <Source id="geo_data" type="geojson" data={overlay}>
                    <Layer {...geoLayer} />
                  </Source>
      </Map>       

    </div>
    
  );
}

export{MapBox}