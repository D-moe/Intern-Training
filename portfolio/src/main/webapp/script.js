// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */

 //random image generator 
window.onload = function(){ 
 document.getElementById("rFrontTile").addEventListener("mouseover", 
 function() {
  getRandomFile(document.querySelector("#rBackTile img"), "images/random/", "test", 4)
 });}

function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
 //Precondition: Assume directory has images of form dirNameXX.jpg where XX is an integer 
function getRandomFile(element, path, fileName, numElems){
  

  //would be ideal if files didn't have to follow a certain naming convention,
  //but not sure this is possible with strictly server side JavaScript :(

  //for now copy directly the example code with minor modifications
  var numElemsInt = parseInt(numElems, 10);
  console.log(numElemsInt);
  var randomFile = path +fileName+(Math.floor(Math.random() * numElemsInt) + 1)+".jpg";
  element.src = randomFile; 
  console.log(randomFile);
}
