const argv = require('yargs')
  .option('c', {
    description: 'Number of calls to queue',
    alias: 'count',
    default: 100,
    nargs: 1
  })
  .option('s', {
    description: 'Server address (e.g. http://localhost:8090)',
    alias: 'server',
    nargs: 1,
    demandOption: true
  }).argv;
const axios = require('axios');

var start = new Date();
var arr = [];
var dupes = 0;
var promises = [];
for (var i=0;i<argv.c;i++) {
  promises.push(axios.get(`${argv.s}/listings?c=${Math.random()}`)
    .then(result => report(result))
    .catch(err => console.log(err)));
}



toodles();
async function toodles() {
  try {
    await Promise.all(promises)
  } catch (error) {
    this.errormsg = error.message;
  } finally {
    this.loading = false
    console.log(`Unique entires: ${arr.length}: duplicates: ${dupes}`)
    var end = new Date();
    console.log(`Start: ${start}, end: ${end}`)
  }
}
function report(result) {
  var d = result.data;
  for (l in d) {
    var id = d[l].id;
    if (arr.indexOf(id) == -1) {
      arr.push(id);
    } else {
      dupes++;
    }
  }
}