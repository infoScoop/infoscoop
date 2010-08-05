/*
add some useful methods to the javascript array class. All operating on the built-in Array class, so no
need for any namespacing object.

Dave Crane 2005
*/

/*
append to end of array, optionally checking for duplicates
*/
Array.prototype.append=function(obj,nodup){
  if (!(nodup && this.contains(obj))){
    this[this.length]=obj;
  }
}

/*
return index of element in the array
*/
Array.prototype.indexOf=function(obj){
  var result=-1;
  for (var i=0;i<this.length;i++){
    if (this[i]==obj){
      result=i;
      break;
    }
  }
  return result;
}

/*
return true if element is in the array
*/
Array.prototype.contains=function(obj){
  return (this.indexOf(obj)>=0);
}

/*
empty the array
*/
Array.prototype.clear=function(){
  this.length=0;
}

/*
insert element at given position in the array, bumping all
subsequent members up one index
*/
Array.prototype.insertAt=function(index,obj){
  this.splice(index,0,obj);
}

/*
remove element at given index
*/
Array.prototype.removeAt=function(index){
  this.splice(index,1);
}

/*
return index of element in the array
*/
Array.prototype.remove=function(obj){
  var index=this.indexOf(obj);
  if (index>=0){
    this.removeAt(index);
  }
}

