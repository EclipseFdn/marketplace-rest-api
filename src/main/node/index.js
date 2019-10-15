const axios = require('axios');
const randomWords = require('random-words');
const uuid = require('uuid');
const argv = require('yargs')
  .option('c', {
    description: 'Number of listings to generate',
    alias: 'count',
    default: 5000,
    nargs: 1
  })
  .option('s', {
    description: 'Server address (e.g. http://localhost:8090)',
    alias: 'server',
    nargs: 1,
    demandOption: true
  }).argv;

let max = argv.c;
const lic_types = ["EPL-2.0","EPL-1.0","GPL"];
const platforms = ["windows","macos","linux"];
const eclipseVs = ["4.6","4.7","4.8","4.9","4.10","4.11","4.12"];
const javaVs = ["1.5", "1.6", "1.7", "1.8", "1.9", "1.10"];
const categoryIds = [];
for (var i=0;i<20;i++) {
  categoryIds.push(uuid.v4());
}
const marketIds = [];
for (i=0;i<5;i++) {
  marketIds.push(uuid.v4());
}

createListing(0);
createCategory(0);
createMarket(0);

function shuff(arr) {
  var out = Array.from(arr);
  for (var i=0;i<out.length;i++) {
    var curr = out[i];
    var rndIdx = Math.floor(Math.random() * arr.length);
    var next = out[rndIdx];
    out[rndIdx] = curr;
    out[i] = next;
  }
  return out;
}

function splice(arr) {
  var out = [];
  var copy = shuff(arr);
  var c = Math.floor(Math.random() * arr.length - 1) + 1;
  for (var i=0; i<=c;i++) {
    out.push(copy[i]);
  }
  return out;
}

function createListing(count) {
  if (count >= max) {
    return;
  }
  count++;
  axios.post(argv.s+"/listings/", generateJSON(uuid.v4()))
    .then(() => createListing(count))
    .catch(err => console.log(err));
}

function createCategory(count) {
  if (count >= categoryIds.length) {
    return;
  }

  axios.post(argv.s+"/categories/", generateCategoryJSON(categoryIds[count++]))
    .then(() => createCategory(count))
    .catch(err => console.log(err));
}

function createMarket(count) {
  if (count >= marketIds.length) {
    return;
  }

  axios.post(argv.s+"/markets/", generateMarketJSON(marketIds[count++]))
    .then(() => createMarket(count))
    .catch(err => console.log(err));
}

function generateJSON(id) {
  var solutions = [];
  var solsCount = Math.floor(Math.random()*5);
  for (var i=0; i < solsCount; i++) {
    solutions.push({
      "version": i,
      "eclipse_versions": splice(eclipseVs),
      "min_java_version": javaVs[Math.floor(Math.random()*eclipseVs.length)],
      "platforms": splice(platforms)
    });
  }
  
  return {
  	"title": "Sample",
  	"url": "https://jakarta.ee",
  	"foundation_ember": false,
  	"teaser": randomWords({exactly:1, wordsPerString:Math.floor(Math.random()*100)})[0],
  	"body": randomWords({exactly:1, wordsPerString:Math.floor(Math.random()*300)})[0],
    "status": "draft",
  	"support_url": "https://jakarta.ee/about/faq",
  	"license_type": lic_types[Math.floor(Math.random()*lic_types.length)],
  	"authors": [
  		{
  			"full_name": "Martin Lowe",
  	    "username": "autumnfound"
  		}
  	],
    "organization": {
			"name": "Eclipse Foundation",
			"id": 1
		},
  	"tags": [
  		{
  			"name": "Build tools",
  			"id": "1",
  			"url": ""
  		}
  	],
  	"versions": solutions,
  	"category_ids": splice(categoryIds).splice(0,Math.ceil(Math.random()*5)+1)
  };
}

function generateCategoryJSON(id) {
  return {
    "id": id,
    "name": randomWords({exactly:1, wordsPerString:Math.ceil(Math.random()*4)})[0],
    "url": "https://www.eclipse.org"
  };
}

function generateMarketJSON(id) {
  return {
    "id": id,
    "name": randomWords({exactly:1, wordsPerString:Math.ceil(Math.random()*4)})[0],
    "url": "https://www.eclipse.org",
    "category_ids": splice(categoryIds).splice(0,Math.ceil(Math.random()*5)+1)
  };
}
