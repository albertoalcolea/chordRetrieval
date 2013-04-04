/* 
 * Check duplicates in a MongoDB collection
 */

 m = function () {
     emit(this.song, 1);
 }

 r = function (k, vals) {
     return Array.sum(vals);
 }

 res = db.chords.mapReduce(m, r, { out : "dup" });


 /*
  * mongo 'myDB' checkdupes.js
  *
  * In mongodb shell use:
  *   use 'myBD'
  *   db.dup.find({value: {$gt: 1}})
  */
