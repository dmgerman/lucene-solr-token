begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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

begin_comment
comment|/**  * Implementations of<code>BinaryQueryResponseWriter</code> are used to  * write response in binary format  * Functionality is exactly same as its parent class<code>QueryResponseWriter</code  * But it may not implement the<code>write(Writer writer, SolrQueryRequest request, SolrQueryResponse response)</code>  * method    *  */
end_comment

begin_interface
DECL|interface|BinaryQueryResponseWriter
specifier|public
interface|interface
name|BinaryQueryResponseWriter
extends|extends
name|QueryResponseWriter
block|{
comment|/**Use it to write the reponse in a binary format      */
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

