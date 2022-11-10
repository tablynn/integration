import { useEffect, useState, RefObject, Dispatch, SetStateAction, useRef } from 'react';
import Map, { ViewState, ViewStateChangeEvent, MapLayerMouseEvent, Source, Layer, MapRef, PointLike, MapboxGeoJSONFeature, MapProvider } from 'react-map-gl'  
import { FeatureCollection } from 'geojson';

import { myKey } from './private/key'
import {overlayData, geoLayer} from './overlays' 
import mapboxgl from 'mapbox-gl';

const initLon: number =  -71.4128;
const initLat: number =41.8240; 
const initZoom: number = 10;

interface AreaInformation {
  city: string,
  state: string, 
  name: string
}

interface ViewStateType{
  longitude: number,
  latitude: number,
  zoom: number
}

const onClickFunc = (e: MapLayerMouseEvent, mapRef: RefObject<MapRef>, setAreaData: Dispatch<SetStateAction<AreaInformation>> ): void => {
  const bbox: [PointLike, PointLike] = [
    [e.point.x, e.point.y],
    [e.point.x, e.point.y]
  ]
  
  const choosenFeatures = mapRef.current?.queryRenderedFeatures(bbox, {
    layers: ["geo_data"],
  });

  console.log("before if" + mapRef);
  if (choosenFeatures !== undefined){
    const feature: MapboxGeoJSONFeature = choosenFeatures[0]; // bbox
    const featureProperties: any = feature.properties;

    //necessary in case the location doesn't have a name
    const locationName: string = Object.hasOwn(featureProperties, 'name') ? feature.properties?.name : "No data";
    console.log("city is "  );//+ feature.properties?.city);
    setAreaData({
      city: feature.properties?.city,
      state: feature.properties?.state,
      name: locationName
    })
  }
}

interface BoundaryType {
  west: number,
  east: number,
  south: number,
  north: number
}

function getLargerBoundary(viewState: ViewStateType): BoundaryType {
  return {
    west: viewState.longitude - Math.pow(2, 11 - viewState.zoom),
    east: viewState.longitude + Math.pow(2, 11 - viewState.zoom),
    south: viewState.latitude - Math.pow(2, 11 - viewState.zoom),
    north: viewState.latitude + Math.pow(2, 11 - viewState.zoom)
  }
}

let largerBoundary: BoundaryType = getLargerBoundary({longitude: initLon, latitude: initLat, zoom: initZoom})

function updateOverlay(currentBounds: mapboxgl.LngLatBounds |  undefined, viewState: ViewStateType, 
  setOverlay: Dispatch<SetStateAction<FeatureCollection | undefined>>): void{
  if (currentBounds !== undefined && 
    (currentBounds.getWest() < largerBoundary.west|| currentBounds.getEast() > largerBoundary.east ||
    currentBounds.getSouth() < largerBoundary.south || currentBounds.getNorth() > largerBoundary.north)) {
     //)
    }
}


 


export default function MapBox() {
  // Attributes of Mapbox
  const [viewState, setViewState] = useState<ViewState>({
    longitude: initLon,
    latitude: initLat,
    zoom: initZoom,
    bearing: 0,
    pitch: 0,
    padding: {top: 1, bottom: 20, left: 1, right: 1}
  });  

  const mapRef: React.RefObject<MapRef> = useRef<MapRef>(null);
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(undefined)

  const [areaData, setAreaData] = useState({
    city: "No selection",
    state: "No selection", 
    name: "No selection"
  })

  //const locationLabel: string = `City: ${areaData.city}, State: ${areaData.state}, Name: ${areaData.name}`;
  //const currentMapPosition: string = `Current latitude: ${viewState.latitude.toFixed(4)} | Current longitude: ${viewState.longitude.toFixed(4)} | Current zoom: ${viewState.zoom.toFixed(4)}`;

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
        onClick={(ev: MapLayerMouseEvent) => onClickFunc(ev, mapRef, setAreaData)} //console.log(ev)}
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