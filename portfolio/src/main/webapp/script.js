// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy of
// the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations under
// the License.

// random image generator
window.onload = SetImageOnMouseOver;

/**
 * Change the given image to a random one whenever the user hovers over
 */
function SetImageOnMouseOver() {
  document.getElementById('rand-front-tile')
      .addEventListener('mouseover', function() {
        getRandomFile(
            /* element= */ document.querySelector('#rand-back-tile img'),
            /* path= */ 'images/random/', /* fileName= */ 'test',
            /* numElems= */ 4)
      });
}

/**
 *
 * @param {string} element - which DOM element to modify
 * @param {string} path - the path starting from the webapp/ directory that the
 *     files are located in
 * @param {string} fileName - the startPrefix of the files in this directory
 *     (assumed all files of the form fileNameXX.jpg)
 * @param {int} numElems - the number of elements in the given directory
 */
function getRandomFile(element, path, fileName, numElems) {
  // It would be ideal if files didn't have to follow a certain naming
  // convention, but not sure this is possible with strictly server side
  // JavaScript. After conferring with help line seems this is indeed
  // impossible.
  // TODO(morleyd): Implement on server side to clean up need for numElems, etc
  // in the directory For now copy directly the example code with minor
  // modifications.

  var randomFile =
      path + fileName + (Math.floor(Math.random() * numElems) + 1) + '.jpg';
  element.src = randomFile;
}
