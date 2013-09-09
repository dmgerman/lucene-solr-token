begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.schema
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|schema
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressCodecs
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
name|SolrTestCaseJ4
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

begin_comment
comment|/**  * Tests things like sorting on docvalues with missing values  */
end_comment

begin_class
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|}
argument_list|)
comment|// old formats cannot represent missing values
DECL|class|DocValuesMissingTest
specifier|public
class|class
name|DocValuesMissingTest
extends|extends
name|SolrTestCaseJ4
block|{
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-docValuesMissing.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** numeric default lucene sort (relative to presumed default value of 0) */
DECL|method|checkSortMissingDefault
specifier|private
name|void
name|checkSortMissingDefault
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|negative
parameter_list|,
specifier|final
name|String
name|positive
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|field
argument_list|,
name|negative
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
name|positive
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"sort"
argument_list|,
name|field
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=0]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=2]"
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
literal|"sort"
argument_list|,
name|field
operator|+
literal|" desc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=0]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=1]"
argument_list|)
expr_stmt|;
block|}
comment|/** sort missing always first */
DECL|method|checkSortMissingFirst
specifier|private
name|void
name|checkSortMissingFirst
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|low
parameter_list|,
specifier|final
name|String
name|high
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|field
argument_list|,
name|low
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
name|high
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"sort"
argument_list|,
name|field
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=0]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=2]"
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
literal|"sort"
argument_list|,
name|field
operator|+
literal|" desc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=0]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=1]"
argument_list|)
expr_stmt|;
block|}
comment|/** sort missing always last */
DECL|method|checkSortMissingLast
specifier|private
name|void
name|checkSortMissingLast
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|low
parameter_list|,
specifier|final
name|String
name|high
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|field
argument_list|,
name|low
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
name|high
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"sort"
argument_list|,
name|field
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=0]"
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
literal|"sort"
argument_list|,
name|field
operator|+
literal|" desc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=0]"
argument_list|)
expr_stmt|;
block|}
comment|/** function query based on missing */
DECL|method|checkSortMissingFunction
specifier|private
name|void
name|checkSortMissingFunction
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|low
parameter_list|,
specifier|final
name|String
name|high
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|field
argument_list|,
name|low
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
name|high
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"fl"
argument_list|,
literal|"e:exists("
operator|+
name|field
operator|+
literal|")"
argument_list|,
literal|"sort"
argument_list|,
literal|"id asc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/bool[@name='e'][.='false']"
argument_list|,
literal|"//result/doc[2]/bool[@name='e'][.='true']"
argument_list|,
literal|"//result/doc[3]/bool[@name='e'][.='true']"
argument_list|)
expr_stmt|;
block|}
comment|/** missing facet count */
DECL|method|checkSortMissingFacet
specifier|private
name|void
name|checkSortMissingFacet
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|String
name|low
parameter_list|,
specifier|final
name|String
name|high
parameter_list|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
name|low
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|field
argument_list|,
name|high
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
name|field
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.missing"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/int[@name='"
operator|+
name|low
operator|+
literal|"'][.=1]"
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/int[@name='"
operator|+
name|high
operator|+
literal|"'][.=1]"
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='"
operator|+
name|field
operator|+
literal|"']/int[.=2]"
argument_list|)
expr_stmt|;
block|}
comment|/** float with default lucene sort (treats as 0) */
DECL|method|testFloatSort
specifier|public
name|void
name|testFloatSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic float with default lucene sort (treats as 0) */
DECL|method|testDynFloatSort
specifier|public
name|void
name|testDynFloatSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"dyn_floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** float with sort missing always first */
DECL|method|testFloatSortMissingFirst
specifier|public
name|void
name|testFloatSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"floatdv_missingfirst"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic float with sort missing always first */
DECL|method|testDynFloatSortMissingFirst
specifier|public
name|void
name|testDynFloatSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_floatdv_missingfirst"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** float with sort missing always last */
DECL|method|testFloatSortMissingLast
specifier|public
name|void
name|testFloatSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"floatdv_missinglast"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic float with sort missing always last */
DECL|method|testDynFloatSortMissingLast
specifier|public
name|void
name|testDynFloatSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_floatdv_missinglast"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** float function query based on missing */
DECL|method|testFloatMissingFunction
specifier|public
name|void
name|testFloatMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dyanmic float function query based on missing */
DECL|method|testDynFloatMissingFunction
specifier|public
name|void
name|testDynFloatMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** float missing facet count */
DECL|method|testFloatMissingFacet
specifier|public
name|void
name|testFloatMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic float missing facet count */
DECL|method|testDynFloatMissingFacet
specifier|public
name|void
name|testDynFloatMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"dyn_floatdv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** int with default lucene sort (treats as 0) */
DECL|method|testIntSort
specifier|public
name|void
name|testIntSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic int with default lucene sort (treats as 0) */
DECL|method|testDynIntSort
specifier|public
name|void
name|testDynIntSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"dyn_intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** int with sort missing always first */
DECL|method|testIntSortMissingFirst
specifier|public
name|void
name|testIntSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"intdv_missingfirst"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic int with sort missing always first */
DECL|method|testDynIntSortMissingFirst
specifier|public
name|void
name|testDynIntSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_intdv_missingfirst"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** int with sort missing always last */
DECL|method|testIntSortMissingLast
specifier|public
name|void
name|testIntSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"intdv_missinglast"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic int with sort missing always last */
DECL|method|testDynIntSortMissingLast
specifier|public
name|void
name|testDynIntSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_intdv_missinglast"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** int function query based on missing */
DECL|method|testIntMissingFunction
specifier|public
name|void
name|testIntMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic int function query based on missing */
DECL|method|testDynIntMissingFunction
specifier|public
name|void
name|testDynIntMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** int missing facet count */
DECL|method|testIntMissingFacet
specifier|public
name|void
name|testIntMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic int missing facet count */
DECL|method|testDynIntMissingFacet
specifier|public
name|void
name|testDynIntMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"dyn_intdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** double with default lucene sort (treats as 0) */
DECL|method|testDoubleSort
specifier|public
name|void
name|testDoubleSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic double with default lucene sort (treats as 0) */
DECL|method|testDynDoubleSort
specifier|public
name|void
name|testDynDoubleSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"dyn_doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** double with sort missing always first */
DECL|method|testDoubleSortMissingFirst
specifier|public
name|void
name|testDoubleSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"doubledv_missingfirst"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic double with sort missing always first */
DECL|method|testDynDoubleSortMissingFirst
specifier|public
name|void
name|testDynDoubleSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_doubledv_missingfirst"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** double with sort missing always last */
DECL|method|testDoubleSortMissingLast
specifier|public
name|void
name|testDoubleSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"doubledv_missinglast"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic double with sort missing always last */
DECL|method|testDynDoubleSortMissingLast
specifier|public
name|void
name|testDynDoubleSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_doubledv_missinglast"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** double function query based on missing */
DECL|method|testDoubleMissingFunction
specifier|public
name|void
name|testDoubleMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dyanmic double function query based on missing */
DECL|method|testDynDoubleMissingFunction
specifier|public
name|void
name|testDynDoubleMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** double missing facet count */
DECL|method|testDoubleMissingFacet
specifier|public
name|void
name|testDoubleMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic double missing facet count */
DECL|method|testDynDoubleMissingFacet
specifier|public
name|void
name|testDynDoubleMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"dyn_doubledv"
argument_list|,
literal|"-1.3"
argument_list|,
literal|"4.2"
argument_list|)
expr_stmt|;
block|}
comment|/** long with default lucene sort (treats as 0) */
DECL|method|testLongSort
specifier|public
name|void
name|testLongSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic long with default lucene sort (treats as 0) */
DECL|method|testDynLongSort
specifier|public
name|void
name|testDynLongSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"dyn_longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** long with sort missing always first */
DECL|method|testLongSortMissingFirst
specifier|public
name|void
name|testLongSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"longdv_missingfirst"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic long with sort missing always first */
DECL|method|testDynLongSortMissingFirst
specifier|public
name|void
name|testDynLongSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_longdv_missingfirst"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** long with sort missing always last */
DECL|method|testLongSortMissingLast
specifier|public
name|void
name|testLongSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"longdv_missinglast"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic long with sort missing always last */
DECL|method|testDynLongSortMissingLast
specifier|public
name|void
name|testDynLongSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_longdv_missinglast"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** long function query based on missing */
DECL|method|testLongMissingFunction
specifier|public
name|void
name|testLongMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic long function query based on missing */
DECL|method|testDynLongMissingFunction
specifier|public
name|void
name|testDynLongMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** long missing facet count */
DECL|method|testLongMissingFacet
specifier|public
name|void
name|testLongMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic long missing facet count */
DECL|method|testDynLongMissingFacet
specifier|public
name|void
name|testDynLongMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"dyn_longdv"
argument_list|,
literal|"-1"
argument_list|,
literal|"4"
argument_list|)
expr_stmt|;
block|}
comment|/** date with default lucene sort (treats as 1970) */
DECL|method|testDateSort
specifier|public
name|void
name|testDateSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic date with default lucene sort (treats as 1970) */
DECL|method|testDynDateSort
specifier|public
name|void
name|testDynDateSort
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingDefault
argument_list|(
literal|"dyn_datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** date with sort missing always first */
DECL|method|testDateSortMissingFirst
specifier|public
name|void
name|testDateSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"datedv_missingfirst"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic date with sort missing always first */
DECL|method|testDynDateSortMissingFirst
specifier|public
name|void
name|testDynDateSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_datedv_missingfirst"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** date with sort missing always last */
DECL|method|testDateSortMissingLast
specifier|public
name|void
name|testDateSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"datedv_missinglast"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic date with sort missing always last */
DECL|method|testDynDateSortMissingLast
specifier|public
name|void
name|testDynDateSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_datedv_missinglast"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** date function query based on missing */
DECL|method|testDateMissingFunction
specifier|public
name|void
name|testDateMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic date function query based on missing */
DECL|method|testDynDateMissingFunction
specifier|public
name|void
name|testDynDateMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** date missing facet count */
DECL|method|testDateMissingFacet
specifier|public
name|void
name|testDateMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic date missing facet count */
DECL|method|testDynDateMissingFacet
specifier|public
name|void
name|testDynDateMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFacet
argument_list|(
literal|"dyn_datedv"
argument_list|,
literal|"1900-12-31T23:59:59.999Z"
argument_list|,
literal|"2005-12-31T23:59:59.999Z"
argument_list|)
expr_stmt|;
block|}
comment|/** string (and dynamic string) with default lucene sort (treats as "") */
DECL|method|testStringSort
specifier|public
name|void
name|testStringSort
parameter_list|()
throws|throws
name|Exception
block|{
comment|// note: cant use checkSortMissingDefault because
comment|// nothing sorts lower then the default of ""
for|for
control|(
name|String
name|field
range|:
operator|new
name|String
index|[]
block|{
literal|"stringdv"
block|,
literal|"dyn_stringdv"
block|}
control|)
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|field
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|field
argument_list|,
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"sort"
argument_list|,
name|field
operator|+
literal|" asc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=0]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=2]"
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
literal|"sort"
argument_list|,
name|field
operator|+
literal|" desc"
argument_list|)
argument_list|,
literal|"//result/doc[1]/str[@name='id'][.=2]"
argument_list|,
literal|"//result/doc[2]/str[@name='id'][.=1]"
argument_list|,
literal|"//result/doc[3]/str[@name='id'][.=0]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** string with sort missing always first */
DECL|method|testStringSortMissingFirst
specifier|public
name|void
name|testStringSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"stringdv_missingfirst"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic string with sort missing always first */
DECL|method|testDynStringSortMissingFirst
specifier|public
name|void
name|testDynStringSortMissingFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFirst
argument_list|(
literal|"dyn_stringdv_missingfirst"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** string with sort missing always last */
DECL|method|testStringSortMissingLast
specifier|public
name|void
name|testStringSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"stringdv_missinglast"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic string with sort missing always last */
DECL|method|testDynStringSortMissingLast
specifier|public
name|void
name|testDynStringSortMissingLast
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingLast
argument_list|(
literal|"dyn_stringdv_missinglast"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** string function query based on missing */
DECL|method|testStringMissingFunction
specifier|public
name|void
name|testStringMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"stringdv"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** dynamic string function query based on missing */
DECL|method|testDynStringMissingFunction
specifier|public
name|void
name|testDynStringMissingFunction
parameter_list|()
throws|throws
name|Exception
block|{
name|checkSortMissingFunction
argument_list|(
literal|"dyn_stringdv"
argument_list|,
literal|"a"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
block|}
comment|/** string missing facet count */
DECL|method|testStringMissingFacet
specifier|public
name|void
name|testStringMissingFacet
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// missing
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
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
argument_list|,
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"stringdv"
argument_list|,
literal|"facet.mincount"
argument_list|,
literal|"1"
argument_list|,
literal|"facet.missing"
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='stringdv']/int[@name='a'][.=1]"
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='stringdv']/int[@name='z'][.=1]"
argument_list|,
literal|"//lst[@name='facet_fields']/lst[@name='stringdv']/int[.=2]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

