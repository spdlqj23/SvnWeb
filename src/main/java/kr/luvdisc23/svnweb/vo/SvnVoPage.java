package kr.luvdisc23.svnweb.vo;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class SvnVoPage {

    private long totCnt;
    private long rowsPerPage;
    private long currentPage;

    private List<SvnVo> svnVo = new ArrayList<>();
    private PagingVo pagingVo = new PagingVo();

    public void setTotCnt(long totCnt) {
        this.totCnt = totCnt;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public void setRowsPerPage(long rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public void setSvnVo(List<SvnVo> svnVo) {
        this.svnVo = svnVo;
    }

    public long getTotCnt() {
        return totCnt;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public List<SvnVo> getSvnVo() {
        return svnVo;
    }

    public PagingVo getPagingVo() {
        return pagingVo;
    }

    public void makePaging() {
        long totalPage = totCnt / rowsPerPage;
        long mod = totCnt % rowsPerPage;
        if(mod > 0) ++totalPage;
        pagingVo.makePaging(totalPage, currentPage,"/svn" );
    }

    public class PagingVo {

        private long currentPage;

        private Page first;
        private Page prev;
        private List<Page> pageList = new ArrayList<>();
        private Page next;
        private Page last;

        public long getCurrentPage() {
            return currentPage;
        }
        public Page getFirst() {
            return first;
        }
        public Page getPrev() {
            return prev;
        }
        public List<Page> getPageList() {
            return pageList;
        }
        public Page getNext() {
            return next;
        }
        public Page getLast() {
            return last;
        }

        public void makePaging(long totalPage, long currentPage, String url) {
            this.currentPage = currentPage;
            long pagingSize = 5;

            if(totalPage <= pagingSize) {
                for(int i = 1; i <= totalPage; i++) {
                    pageList.add(new Page(url, i) );
                }
            }else {
                long middlePoint = pagingSize / 2;
                long start = currentPage - middlePoint;
                long end = currentPage + middlePoint;
                Range<Long> range = Range.between(1L, totalPage);

                if(range.contains(start) && range.contains(end)) {
                    for (long i = start; i <= end; i++) {
                        Page page = new Page(url, i);
                        if(i == currentPage){
                            page.setCurrent(true);
                        }
                        pageList.add(page);
                    }

                    if(start > 2) {
                        first = new Page(url, 1);
                        prev = new Page(url, start - 1);
                    }else if(start == 2){
                        prev = new Page(url, start - 1);
                    }

                    if(end < totalPage - 1) {
                        next = new Page(url, end + 1);
                        last = new Page(url, totalPage);
                    }else if(end == totalPage - 1) {
                        next = new Page(url, end + 1);
                    }

                }else if(range.contains(start)) {
                    for (long i = totalPage - pagingSize; i <= totalPage; i++) {
                        Page page = new Page(url, i);
                        if(i == currentPage){
                            page.setCurrent(true);
                        }
                        pageList.add(page);
                    }

                    if(totalPage - pagingSize > 2) {
                        first = new Page(url, 1);
                        prev = new Page(url, totalPage - pagingSize - 1);
                    }else if(totalPage - pagingSize == 2){
                        prev = new Page(url, totalPage - pagingSize - 1);
                    }
                }else if(range.contains(end)) {
                    for (long i = 1; i <= pagingSize; i++) {
                        Page page = new Page(url, i);
                        if(i == currentPage){
                            page.setCurrent(true);
                        }
                        pageList.add(page);
                    }

                    if(pagingSize < totalPage - 1) {
                        next = new Page(url, pagingSize + 1);
                        last = new Page(url, totalPage);
                    }else if(pagingSize == totalPage - 1) {
                        next = new Page(url, pagingSize + 1);
                    }
                }
            }
        }

        public class Page {
            private String url;
            private long page;
            private boolean current;

            public Page() {}
            public Page(String url, long page) {
                this.url = url;
                this.page = page;
            }
            public String getUrl() {
                return url;
            }
            public void setUrl(String url) {
                this.url = url;
            }
            public long getPage() {
                return page;
            }
            public void setPage(long page) {
                this.page = page;
            }
            public boolean isCurrent() {
                return current;
            }
            public void setCurrent(boolean current) {
                this.current = current;
            }
        }
    }
}
