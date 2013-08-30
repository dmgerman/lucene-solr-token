begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|join
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
operator|.
name|SolrQueryRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|QParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SyntaxError
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
import|;
end_import

begin_class
DECL|class|BJQParserTest
specifier|public
class|class
name|BJQParserTest
extends|extends
name|SolrTestCaseJ4
block|{
DECL|field|klm
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|klm
init|=
operator|new
name|String
index|[]
block|{
literal|"k"
block|,
literal|"l"
block|,
literal|"m"
block|}
decl_stmt|;
DECL|field|xyz
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|xyz
init|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"y"
argument_list|,
literal|"z"
argument_list|)
decl_stmt|;
DECL|field|abcdef
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|abcdef
init|=
operator|new
name|String
index|[]
block|{
literal|"a"
block|,
literal|"b"
block|,
literal|"c"
block|,
literal|"d"
block|,
literal|"e"
block|,
literal|"f"
block|}
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema15.xml"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
specifier|static
name|void
name|createIndex
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
name|blocks
init|=
name|createBlocks
argument_list|()
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|block
range|:
name|blocks
control|)
block|{
name|List
argument_list|<
name|XmlDoc
argument_list|>
name|updBlock
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
index|[]
name|doc
range|:
name|block
control|)
block|{
name|String
index|[]
name|idDoc
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|doc
argument_list|,
name|doc
operator|.
name|length
operator|+
literal|2
argument_list|)
decl_stmt|;
name|idDoc
index|[
name|doc
operator|.
name|length
index|]
operator|=
literal|"id"
expr_stmt|;
name|idDoc
index|[
name|doc
operator|.
name|length
operator|+
literal|1
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|updBlock
operator|.
name|add
argument_list|(
name|doc
argument_list|(
name|idDoc
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
comment|//got xmls for every doc. now nest all into the last one
name|XmlDoc
name|parentDoc
init|=
name|updBlock
operator|.
name|get
argument_list|(
name|updBlock
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|parentDoc
operator|.
name|xml
operator|=
name|parentDoc
operator|.
name|xml
operator|.
name|replace
argument_list|(
literal|"</doc>"
argument_list|,
name|updBlock
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|updBlock
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\[\\]]"
argument_list|,
literal|""
argument_list|)
operator|+
literal|"</doc>"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|add
argument_list|(
name|parentDoc
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// force empty segment (actually, this will no longer create an empty segment, only a new segments_n)
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='"
operator|+
name|i
operator|+
literal|"']"
argument_list|)
expr_stmt|;
comment|/*      * dump docs well System.out.println(h.query(req("q","*:*",      * "sort","_docid_ asc", "fl",      * "parent_s,child_s,parentchild_s,grand_s,grand_child_s,grand_parentchild_s"      * , "wt","csv", "rows","1000"))); /      */
block|}
DECL|field|id
specifier|private
specifier|static
name|int
name|id
init|=
literal|0
decl_stmt|;
DECL|method|createBlocks
specifier|private
specifier|static
name|List
argument_list|<
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
name|createBlocks
parameter_list|()
block|{
name|List
argument_list|<
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|String
index|[]
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|parent
range|:
name|abcdef
control|)
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|block
init|=
name|createChildrenBlock
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|block
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"parent_s"
block|,
name|parent
block|}
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|blocks
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|blocks
return|;
block|}
DECL|method|createChildrenBlock
specifier|private
specifier|static
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|createChildrenBlock
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|block
init|=
operator|new
name|ArrayList
argument_list|<
name|String
index|[]
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|child
range|:
name|klm
control|)
block|{
name|block
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"child_s"
block|,
name|child
block|,
literal|"parentchild_s"
block|,
name|parent
operator|+
name|child
block|}
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|block
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
name|addGrandChildren
argument_list|(
name|block
argument_list|)
expr_stmt|;
return|return
name|block
return|;
block|}
DECL|method|addGrandChildren
specifier|private
specifier|static
name|void
name|addGrandChildren
parameter_list|(
name|List
argument_list|<
name|String
index|[]
argument_list|>
name|block
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|grandChildren
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|xyz
argument_list|)
decl_stmt|;
comment|// add grandchildren after children
for|for
control|(
name|ListIterator
argument_list|<
name|String
index|[]
argument_list|>
name|iter
init|=
name|block
operator|.
name|listIterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
index|[]
name|child
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|child_s
init|=
name|child
index|[
literal|1
index|]
decl_stmt|;
name|String
name|parentchild_s
init|=
name|child
index|[
literal|3
index|]
decl_stmt|;
name|int
name|grandChildPos
init|=
literal|0
decl_stmt|;
name|boolean
name|lastLoopButStillHasGrCh
init|=
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|grandChildren
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|grandChildren
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
operator|(
name|grandChildPos
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|grandChildren
operator|.
name|size
argument_list|()
operator|*
literal|2
argument_list|)
operator|)
operator|<
name|grandChildren
operator|.
name|size
argument_list|()
operator|||
name|lastLoopButStillHasGrCh
operator|)
condition|)
block|{
name|grandChildPos
operator|=
name|grandChildPos
operator|>=
name|grandChildren
operator|.
name|size
argument_list|()
condition|?
literal|0
else|:
name|grandChildPos
expr_stmt|;
name|iter
operator|.
name|add
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"grand_s"
block|,
name|grandChildren
operator|.
name|remove
argument_list|(
name|grandChildPos
argument_list|)
block|,
literal|"grand_child_s"
block|,
name|child_s
block|,
literal|"grand_parentchild_s"
block|,
name|parentchild_s
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|// and reverse after that
name|Collections
operator|.
name|reverse
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFull
specifier|public
name|void
name|testFull
parameter_list|()
throws|throws
name|IOException
throws|,
name|Exception
block|{
name|String
name|childb
init|=
literal|"{!parent which=\"parent_s:[* TO *]\"}child_s:l"
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
name|childb
argument_list|)
argument_list|,
name|sixParents
argument_list|)
expr_stmt|;
block|}
DECL|field|sixParents
specifier|private
specifier|static
specifier|final
name|String
name|sixParents
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='6']"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='a'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='b'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='c'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='d'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='e'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='f'"
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testJustParentsFilter
specifier|public
name|void
name|testJustParentsFilter
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!parent which=\"parent_s:[* TO *]\"}"
argument_list|)
argument_list|,
name|sixParents
argument_list|)
expr_stmt|;
block|}
DECL|field|beParents
specifier|private
specifier|final
specifier|static
name|String
name|beParents
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"//*[@numFound='2']"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='b'"
block|,
literal|"//doc/arr[@name=\"parent_s\"]/str='e'"
block|}
decl_stmt|;
annotation|@
name|Test
DECL|method|testIntersectBqBjq
specifier|public
name|void
name|testIntersectBqBjq
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+parent_s:(e b) +_query_:\"{!parent which=$pq v=$chq}\""
argument_list|,
literal|"chq"
argument_list|,
literal|"child_s:l"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"fq"
argument_list|,
literal|"{!parent which=$pq v=$chq}\""
argument_list|,
literal|"q"
argument_list|,
literal|"parent_s:(e b)"
argument_list|,
literal|"chq"
argument_list|,
literal|"child_s:l"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!parent which=$pq v=$chq}\""
argument_list|,
literal|"fq"
argument_list|,
literal|"parent_s:(e b)"
argument_list|,
literal|"chq"
argument_list|,
literal|"child_s:l"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFq
specifier|public
name|void
name|testFq
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!parent which=$pq v=$chq}"
argument_list|,
literal|"fq"
argument_list|,
literal|"parent_s:(e b)"
argument_list|,
literal|"chq"
argument_list|,
literal|"child_s:l"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
comment|// ,"debugQuery","on"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
name|boolean
name|qfq
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
name|qfq
condition|?
literal|"q"
else|:
literal|"fq"
argument_list|,
literal|"parent_s:(a e b)"
argument_list|,
operator|(
operator|!
name|qfq
operator|)
condition|?
literal|"q"
else|:
literal|"fq"
argument_list|,
literal|"{!parent which=$pq v=$chq}"
argument_list|,
literal|"chq"
argument_list|,
literal|"parentchild_s:(bm ek cl)"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIntersectParentBqChildBq
specifier|public
name|void
name|testIntersectParentBqChildBq
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+parent_s:(a e b) +_query_:\"{!parent which=$pq v=$chq}\""
argument_list|,
literal|"chq"
argument_list|,
literal|"parentchild_s:(bm ek cl)"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"SOLR-5168"
argument_list|)
DECL|method|testGrandChildren
specifier|public
name|void
name|testGrandChildren
parameter_list|()
throws|throws
name|IOException
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!parent which=$parentfilter v=$children}"
argument_list|,
literal|"children"
argument_list|,
literal|"{!parent which=$childrenfilter v=$grandchildren}"
argument_list|,
literal|"grandchildren"
argument_list|,
literal|"grand_s:"
operator|+
literal|"x"
argument_list|,
literal|"parentfilter"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|,
literal|"childrenfilter"
argument_list|,
literal|"child_s:[* TO *]"
argument_list|)
argument_list|,
name|sixParents
argument_list|)
expr_stmt|;
comment|// int loops = atLeast(1);
name|String
name|grandChildren
init|=
name|xyz
operator|.
name|get
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|xyz
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"+parent_s:(a e b) +_query_:\"{!parent which=$pq v=$chq}\""
argument_list|,
literal|"chq"
argument_list|,
literal|"{!parent which=$childfilter v=$grandchq}"
argument_list|,
literal|"grandchq"
argument_list|,
literal|"+grand_s:"
operator|+
name|grandChildren
operator|+
literal|" +grand_parentchild_s:(b* e* c*)"
argument_list|,
literal|"pq"
argument_list|,
literal|"parent_s:[* TO *]"
argument_list|,
literal|"childfilter"
argument_list|,
literal|"child_s:[* TO *]"
argument_list|)
argument_list|,
name|beParents
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChildrenParser
specifier|public
name|void
name|testChildrenParser
parameter_list|()
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!child of=\"parent_s:[* TO *]\"}parent_s:a"
argument_list|,
literal|"fq"
argument_list|,
literal|"NOT grand_s:[* TO *]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|,
literal|"//doc/arr[@name=\"child_s\"]/str='k'"
argument_list|,
literal|"//doc/arr[@name=\"child_s\"]/str='l'"
argument_list|,
literal|"//doc/arr[@name=\"child_s\"]/str='m'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!child of=\"parent_s:[* TO *]\"}parent_s:b"
argument_list|,
literal|"fq"
argument_list|,
literal|"-parentchild_s:bm"
argument_list|,
literal|"fq"
argument_list|,
literal|"-grand_s:*"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|,
literal|"//doc/arr[@name=\"child_s\"]/str='k'"
argument_list|,
literal|"//doc/arr[@name=\"child_s\"]/str='l'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCacheHit
specifier|public
name|void
name|testCacheHit
parameter_list|()
throws|throws
name|IOException
block|{
name|SolrCache
name|parentFilterCache
init|=
operator|(
name|SolrCache
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"perSegFilter"
argument_list|)
decl_stmt|;
name|SolrCache
name|filterCache
init|=
operator|(
name|SolrCache
operator|)
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getInfoRegistry
argument_list|()
operator|.
name|get
argument_list|(
literal|"filterCache"
argument_list|)
decl_stmt|;
name|NamedList
name|parentsBefore
init|=
name|parentFilterCache
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
name|NamedList
name|filtersBefore
init|=
name|filterCache
operator|.
name|getStatistics
argument_list|()
decl_stmt|;
comment|// it should be weird enough to be uniq
name|String
name|parentFilter
init|=
literal|"parent_s:([a TO c] [d TO f])"
decl_stmt|;
name|assertQ
argument_list|(
literal|"search by parent filter"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!parent which=\""
operator|+
name|parentFilter
operator|+
literal|"\"}"
argument_list|)
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"filter by parent filter"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!parent which=\""
operator|+
name|parentFilter
operator|+
literal|"\"}"
argument_list|)
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"didn't hit fqCache yet "
argument_list|,
literal|0L
argument_list|,
name|delta
argument_list|(
literal|"hits"
argument_list|,
name|filterCache
operator|.
name|getStatistics
argument_list|()
argument_list|,
name|filtersBefore
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"filter by join"
argument_list|,
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!parent which=\""
operator|+
name|parentFilter
operator|+
literal|"\"}child_s:l"
argument_list|)
argument_list|,
literal|"//*[@numFound='6']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"in cache mode every request lookups"
argument_list|,
literal|3
argument_list|,
name|delta
argument_list|(
literal|"lookups"
argument_list|,
name|parentFilterCache
operator|.
name|getStatistics
argument_list|()
argument_list|,
name|parentsBefore
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"last two lookups causes hits"
argument_list|,
literal|2
argument_list|,
name|delta
argument_list|(
literal|"hits"
argument_list|,
name|parentFilterCache
operator|.
name|getStatistics
argument_list|()
argument_list|,
name|parentsBefore
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the first lookup gets insert"
argument_list|,
literal|1
argument_list|,
name|delta
argument_list|(
literal|"inserts"
argument_list|,
name|parentFilterCache
operator|.
name|getStatistics
argument_list|()
argument_list|,
name|parentsBefore
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true join query is cached in fqCache"
argument_list|,
literal|1L
argument_list|,
name|delta
argument_list|(
literal|"lookups"
argument_list|,
name|filterCache
operator|.
name|getStatistics
argument_list|()
argument_list|,
name|filtersBefore
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|delta
specifier|private
name|long
name|delta
parameter_list|(
name|String
name|key
parameter_list|,
name|NamedList
name|a
parameter_list|,
name|NamedList
name|b
parameter_list|)
block|{
return|return
operator|(
name|Long
operator|)
name|a
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|-
operator|(
name|Long
operator|)
name|b
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|nullInit
specifier|public
name|void
name|nullInit
parameter_list|()
block|{
operator|new
name|BlockJoinParentQParserPlugin
argument_list|()
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

