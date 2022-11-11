import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";
import { resourceLimits } from "worker_threads";

// Import the raw JSON file
import rl_data from "./mockData/fullDownload.json";

const ourURL = "http://localhost:3232/";

/**
 * Mocking API call to get redLineData
 * @param minLat -- users minimum latitude
 * @param minLong  -- users minimum longitude
 * @param maxLat  -- users maximum latitude
 * @param maxLong  -- users maximum longitude
 * @returns -- returns the redlineData or undefined if the data is not valid
 */

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

/**
 * Thpe predicate for feature collection
 * @param json -- json file
 * @returns -- data relating to FeatureCollection
 */

function isFeatureCollection(json: any): json is FeatureCollection {
    return json.type === "FeatureCollection"
}

/**
 * checks if the FeatureCollection of the fullDownload JSON is valid 
 * @returns --  fullDown data if valid, undefined if not
 */

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