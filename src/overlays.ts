import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";
import { resourceLimits } from "worker_threads";

// Import the raw JSON file
import rl_data from "./mockData/fullDownload.json";

const ourURL = "http://localhost:3232/";

export function fetchRedLineData(
    minLat: number,
    minLong: number,
    maxLat: number,
    maxLong: number
): Promise<FeatureCollection | undefined> {
    const url: string = ourURL + `redlineData?min_lat=${minLat}&max_lat=${maxLat}&min_lon=${minLong}&max_lon=${maxLong}`;
    return fetch(url)
    .then((result) => result.json())
    .then((json) => {
        if (isFeatureCollection(json.data)){
            return json.data;
        }
        return undefined
    
    });
}

// Type predicate for FeatureCollection
function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type === "FeatureCollection"
}

export function overlayData(): GeoJSON.FeatureCollection | undefined {
  if(isFeatureCollection(rl_data))
    return rl_data
  return undefined
}


////////////////////////////////////

const propertyName = 'holc_grade';

export const geoLayer: FillLayer = {
    id: 'geo_data',
    type: 'fill',
    paint: {
        'fill-color': [
            'match',
            ['get', propertyName],
            'A',
            '#5bcc04',
            'B',
            '#04b8cc',
            'C',
            '#e9ed0e',
            'D',
            '#d11d1d',
             '#ccc'
        ],
        'fill-opacity': 0.2
    }
};