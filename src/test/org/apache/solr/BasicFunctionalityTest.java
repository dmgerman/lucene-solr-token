begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright 2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|request
operator|.
name|*
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests some basic functionality of Solr while demonstrating good  * Best Practices for using AbstractSolrTestCase  */
end_comment

begin_class
DECL|class|BasicFunctionalityTest
specifier|public
class|class
name|BasicFunctionalityTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaPath
specifier|public
name|String
name|getSchemaPath
parameter_list|()
block|{
return|return
literal|"solr/conf/schema.xml"
return|;
block|}
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// if you override setUp or tearDown, you better call
comment|// the super classes version
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testSomeStuff
specifier|public
name|void
name|testSomeStuff
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
literal|"test query on empty index"
argument_list|,
name|req
argument_list|(
literal|"qlkciyopsbgzyvkylsjhchghjrdf"
argument_list|)
argument_list|,
literal|"//result[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test escaping of ";"
name|assertU
argument_list|(
literal|"deleting 42 for no reason at all"
argument_list|,
name|delI
argument_list|(
literal|"42"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"adding doc#42"
argument_list|,
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"aa;bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"does commit work?"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"backslash escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:aa\\;bb"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"quote escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:\"aa;bb\""
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//int[@name='id'][.='42']"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"no escaping semicolon"
argument_list|,
name|req
argument_list|(
literal|"id:42 AND val_s:aa"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"42"
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
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
comment|// test allowDups default of false
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"AAA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"BBB"
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
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='BBB']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"CCC"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"42"
argument_list|,
literal|"val_s"
argument_list|,
literal|"DDD"
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
literal|"id:42"
argument_list|)
argument_list|,
literal|"//*[@numFound='1']"
argument_list|,
literal|"//str[.='DDD']"
argument_list|)
expr_stmt|;
comment|// test deletes
name|String
index|[]
name|adds
init|=
operator|new
name|String
index|[]
block|{
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"105"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"102"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"false"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"103"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"true"
argument_list|)
block|,
name|add
argument_list|(
name|doc
argument_list|(
literal|"id"
argument_list|,
literal|"101"
argument_list|)
argument_list|,
literal|"allowDups"
argument_list|,
literal|"false"
argument_list|)
block|,     }
decl_stmt|;
for|for
control|(
name|String
name|a
range|:
name|adds
control|)
block|{
name|assertU
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
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
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='4']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"102"
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
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='3']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"105"
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
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
literal|"id:[100 TO 110]"
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
literal|"id:[100 TO 110]"
argument_list|)
argument_list|,
literal|"//*[@numFound='0']"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleUpdatesPerAdd
specifier|public
name|void
name|testMultipleUpdatesPerAdd
parameter_list|()
block|{
comment|// big freaking kludge since the response is currently not well formed.
name|String
name|res
init|=
name|h
operator|.
name|update
argument_list|(
literal|"<add><doc><field name=\"id\">1</field></doc><doc><field name=\"id\">2</field></doc></add>"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<result status=\"0\"></result><result status=\"0\"></result>"
argument_list|,
name|res
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"<commit/>"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"id:[0 TO 99]"
argument_list|)
argument_list|,
literal|"//*[@numFound='2']"
argument_list|)
expr_stmt|;
block|}
comment|//   /** this doesn't work, but if it did, this is how we'd test it. */
comment|//   public void testOverwriteFalse() {
comment|//     assertU(adoc("id", "overwrite", "val_s", "AAA"));
comment|//     assertU(commit());
comment|//     assertU(add(doc("id", "overwrite", "val_s", "BBB")
comment|//                 ,"allowDups", "false"
comment|//                 ,"overwriteCommitted","false"
comment|//                 ,"overwritePending","false"
comment|//                 ));
comment|//     assertU(commit());
comment|//     assertQ(req("id:overwrite")
comment|//             ,"//*[@numFound='1']"
comment|//             ,"//str[.='AAA']"
comment|//             );
comment|//   }
block|}
end_class

end_unit

