function makePaginationHTML(listRowCount, pageLinkCount, currentPageIndex, totalListCount, htmlTargetId){
	let targetUI = document.querySelector("#" + htmlTargetId);
	
	let pageCount = Math.ceil(totalListCount/listRowCount);
	
	let startPageIndex = 0;
	if( (currentPageIndex % pageLinkCount) == 0 ){ //10, 20...맨마지막
    startPageIndex = ((currentPageIndex / pageLinkCount)-1)*pageLinkCount + 1
    }else{
        startPageIndex = Math.floor(currentPageIndex / pageLinkCount)*pageLinkCount + 1
    }
		
    let endPageIndex = 0;
    if( (currentPageIndex % pageLinkCount) == 0 ){ //10, 20...맨마지막
        endPageIndex = ((currentPageIndex / pageLinkCount)-1)*pageLinkCount + pageLinkCount
    }else{
        endPageIndex = Math.floor(currentPageIndex / pageLinkCount)*pageLinkCount + pageLinkCount;
    }		
		
	let prev;
    if( currentPageIndex <= pageLinkCount ){
        prev = false;
    }else{
        prev = true;
    }	
	
	let next;
    if(endPageIndex > pageCount){
        endPageIndex = pageCount
        next = false;
    }else{
        next = true;
    }
	
	
	
	let paginationHTML = `<ul class="pagination justify-content-center">`;
	
	if(prev){
		paginationHTML +=
			`<li class="page-item">
			     <a class="page-link" href="javascript:movePage(${startPageIndex-1});" aria-label="Previous">
			     <span aria-hidden="true">&laquo;</span>
			     </a>
			 </li>`;
	}
	
	for(let i = startPageIndex; i<= endPageIndex; i++){
		if(i==currentPageIndex){
			paginationHTML += `<li class="page-item active"><a class="page-link" href="javascript:movePage(${i});">${i}</a></li>`;
		}else{
			paginationHTML += `<li class="page-item"><a class="page-link" href="javascript:movePage(${i});">${i}</a></li>`;
		}
		
	}
	
	if(next){
		paginationHTML +=
			`<li class="page-item">
			     <a class="page-link" href="javascript:movePage(${endPageIndex+1});" aria-label="Previous">
			     <span aria-hidden="true">&raquo;</span>
			     </a>
			 </li>`;
	}
	
	paginationHTML += `</ul>`;
	
	targetUI.innerHTML = paginationHTML;
	}