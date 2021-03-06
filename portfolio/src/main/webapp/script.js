/**
 * Ensure that Url properly reloads on back arrow, prevents error that was
 * occurring from reused upload Url. This may not be the most friendly solution
 * for other browsers look at the following link for reference:
 * https://stackoverflow.com/questions/43043113/how-to-force-reloading-a-page-when-using-browser-back-button
 */
if (performance.navigation.type == 2) {
  updateImageUrl();
}
/**
 * Set initial actions to be run when window first loads.
 */
window.onload = (event) => {
  setImageOnMouseOver();
  loadComments('/data');
};
/**
 * Change the given image to a random one whenever the user hovers over.
 */
function setImageOnMouseOver() {
  document.getElementById('rand-front-tile')
      .addEventListener('mouseover', function() {
        getRandomFile(
            /* element= */ document.querySelector('#rand-back-tile img'),
            /* path= */ 'images/random/', /* fileName= */ 'test',
            /* numElems= */ 4);
      });
}
/**
 * Change the visibility of the comment fields so the user can add a new
 * comment.
 */
function showComment() {
  document.getElementById('add-comment').style.visibility = 'visible';
}

/**
 * Return a random file, currently used to fetch a random image and display on
 * hover.
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
  // in the directory. For now copy directly the example code with minor
  // modifications.
  const computedIndex = (Math.floor(Math.random() * numElems) + 1);
  const randomFile = path + fileName + computedIndex + '.jpg';
  element.src = randomFile;
}
/**
 * Get response from url and modify given element innerHtml.
 * @param {string} path - the path the given query refers to
 * @param {DOM element} element - an element in the DOM for selecting
 */
function fetchAndModify(path, element) {
  fetch(path)
      .then(response => response.text())
        .then(content => element.innerHTML = content);
}
/**
 * Creates an innerHtml element and then reads its text to remove special
 * characters from the input
 * @param {string} inputText The input to remove special characters from to
 *     prevent scripting attacks
 */
function sanitizeHtml(inputText) {
  let convertDiv = document.createElement('div');
  convertDiv.textContent = inputText;
  return convertDiv.innerHTML;
};


/**
 * Fetches the value return by the given data service and then
 * modifies the content of a div to store each of the response values as a
 * paragraph.
 * @param {string} path The url of the data service to use
 */
function fetchAndChangeBody(path) {
  fetch(path).then(response => response.json()).then(json => {
    const target = document.getElementById('comment');
    let newComment = document.createElement('p');
    for (index in json) {
      newComment.innerText = json[index];
      target.appendChild(newComment);
    }
  });
}

/**
 * Fetches the comment data and adds each of them to the loaded
 * content.
 * TODO(morleyd): change individual comment load to be asynchronous to improve
 * performance. It might also make sense to have a hard limit on the number of
 * comments loaded, think about this for future implementations.
 * @param {string} path The path to use when fetching data
 */
function loadComments(path) {
  fetch(path)
      .then(response => response.json())
      .then(
          json => {
            for (index in json) {
              let currentComment = json[index];
              let userName = currentComment['userName'];
              let commentBody = currentComment['commentBody'];
              let blobKey = undefined;
              // we need to check whether a blobKey exists
              console.log(json);
              if (currentComment.hasOwnProperty('blobKey')) {
                blobKey = currentComment['blobKey'];
              }
              addComment(
                  /* name= */ userName, /* content= */ commentBody,
                  /* blobKey= */ blobKey);
            }
          }
      );
}

/**
 * Creates divs for a new comment, intended to be called by
 * loadComments.
 * @param {string} name The name of the user who made the comment
 * @param {string} content The body of the comment
 * @param {string} blobKey The blobKey of an image to add to the comment, this
 *     may be undefined
 */
function addComment(name, content, blobKey) {
  const target = document.getElementById('prior-comments');
  let newComment = document.createElement('div');
  newComment.className = 'comment-full';
  // add an image to the comment only if image is defined
  if (blobKey !== undefined) {
    let img = newComment.appendChild(document.createElement('img'));
    img.className = 'comment-img';
    img.src = '/serve?blob-key=' + blobKey;
  }
  let body = newComment.appendChild(document.createElement('div'));
  body.className = 'comment-body';
  let user = newComment.appendChild(document.createElement('div'));
  user.className = 'sub-script';
  // We need to make sure to strip comments of any special chars they may have
  // in order to prevent injection attacks.
  user.innerText = '-' + sanitizeHtml(/* str= */ name);
  body.innerText = sanitizeHtml(/* str= */ content);

  target.appendChild(newComment);
}
/**
 * Clear all generated comments from database and refetch.
 */
function clearComments() {
  fetch('/deletedata', {
    method: 'POST',
    headers: {'Content-Type': 'application/json;charset=utf-8'},
    body: ''
  })
      .then(() => {
        clearBody();
      })
      .then(() => {
        loadComments('/data?refresh=true');
      });
}

/**
 * Clear body of the comments div, removing all prior comments.
 */
function clearBody() {
  let commentFull = document.querySelectorAll('.comment-full');
  for (const commentContent of commentFull) {
    commentContent.parentNode.removeChild(commentContent);
  }
}

/**
 * Update the image url that is generated for each uploaded image for blobstore.
 */
function updateImageUrl() {
  fetch('/blobstore-upload-url')
      .then((response) => {
        return response.text();
      })
      .then((imageUploadUrl) => {
        const messageForm = document.getElementById('data-fetch');
        messageForm.action = imageUploadUrl;
      });
}
