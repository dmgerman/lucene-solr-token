begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**   * Copyright 2004 The Apache Software Foundation   *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   *     http://www.apache.org/licenses/LICENSE-2.0   *   * Unless required by applicable law or agreed to in writing, software   * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   */
end_comment

begin_package
DECL|package|org.apache.lucene.gdata.server
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
package|;
end_package

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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|server
operator|.
name|GDataRequest
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|gdata
operator|.
name|utils
operator|.
name|DateFormater
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseEntry
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|BaseFeed
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|data
operator|.
name|ExtensionProfile
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|common
operator|.
name|xml
operator|.
name|XmlWriter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gdata
operator|.
name|util
operator|.
name|common
operator|.
name|xml
operator|.
name|XmlWriter
operator|.
name|Namespace
import|;
end_import

begin_comment
comment|/**  * The FeedRequest Class wraps the curren HttpServletResponse. Any action on the  * HttpServletRequest will be executed via this class. This represents an  * abstraction on the plain {@link HttpServletResponse}. Any action which has  * to be performed on the underlaying {@link HttpServletResponse} will be  * executed within this class.  *<p>  * The GData basicly writes two different kinds ouf reponse to the output  * stream.  *<ol>  *<li>update, delete or insert requests will respond with a statuscode and if  * successful the feed entry modified or created</li>  *<li>get requests will respond with a statuscode and if successful the  * requested feed</li>  *</ol>  *   * For this purpose the {@link GDataResponse} class provides the overloaded  * method  * {@link org.apache.lucene.gdata.server.GDataResponse#sendResponse(BaseEntry, ExtensionProfile)}  * which sends the entry e.g feed to the output stream.  *</p>  *<p>  * This class will set the HTTP<tt>Last-Modified</tt> Header to enable  * clients to send<tt>If-Modified-Since</tt> request header to avoid  * retrieving the content again if it hasn't changed. If the content hasn't  * changed since the If-Modified-Since time, then the GData service returns a  * 304 (Not Modified) HTTP response.  *</p>  *   *   *   *   * @author Simon Willnauer  *   */
end_comment

begin_class
DECL|class|GDataResponse
specifier|public
class|class
name|GDataResponse
block|{
DECL|field|error
specifier|private
name|int
name|error
decl_stmt|;
DECL|field|isError
specifier|private
name|boolean
name|isError
init|=
literal|false
decl_stmt|;
DECL|field|encoding
specifier|private
name|String
name|encoding
decl_stmt|;
DECL|field|outputFormat
specifier|private
name|OutputFormat
name|outputFormat
decl_stmt|;
DECL|field|response
specifier|private
specifier|final
name|HttpServletResponse
name|response
decl_stmt|;
DECL|field|XMLMIME_ATOM
specifier|protected
specifier|static
specifier|final
name|String
name|XMLMIME_ATOM
init|=
literal|"text/xml"
decl_stmt|;
DECL|field|XMLMIME_RSS
specifier|protected
specifier|static
specifier|final
name|String
name|XMLMIME_RSS
init|=
literal|"text/xml"
decl_stmt|;
DECL|field|DEFAUL_NAMESPACE_URI
specifier|private
specifier|static
specifier|final
name|String
name|DEFAUL_NAMESPACE_URI
init|=
literal|"http://www.w3.org/2005/Atom"
decl_stmt|;
DECL|field|DEFAULT_NAMESPACE
specifier|private
specifier|static
specifier|final
name|Namespace
name|DEFAULT_NAMESPACE
init|=
operator|new
name|Namespace
argument_list|(
literal|""
argument_list|,
name|DEFAUL_NAMESPACE_URI
argument_list|)
decl_stmt|;
DECL|field|HEADER_LASTMODIFIED
specifier|private
specifier|static
specifier|final
name|String
name|HEADER_LASTMODIFIED
init|=
literal|"Last-Modified"
decl_stmt|;
comment|/**      * Creates a new GDataResponse      *       * @param response -      *            The underlaying {@link HttpServletResponse}      */
DECL|method|GDataResponse
specifier|public
name|GDataResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|response
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"response must not be null"
argument_list|)
throw|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
comment|/**      * Sets an error code to this FeedResponse.      *       * @param errorCode -      *            {@link HttpServletResponse} error code      */
DECL|method|setError
specifier|public
name|void
name|setError
parameter_list|(
name|int
name|errorCode
parameter_list|)
block|{
name|this
operator|.
name|isError
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|error
operator|=
name|errorCode
expr_stmt|;
block|}
comment|/**      * Sets the status of the underlaying response      *       * @see HttpServletResponse      * @param responseCode -      *            the status of the response      */
DECL|method|setResponseCode
specifier|public
name|void
name|setResponseCode
parameter_list|(
name|int
name|responseCode
parameter_list|)
block|{
name|this
operator|.
name|response
operator|.
name|setStatus
argument_list|(
name|responseCode
argument_list|)
expr_stmt|;
block|}
comment|/**      * This method sends the specified error to the user if set      *       * @throws IOException -      *             if an I/O Exception occures      */
DECL|method|sendError
specifier|public
name|void
name|sendError
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|isError
condition|)
name|this
operator|.
name|response
operator|.
name|sendError
argument_list|(
name|this
operator|.
name|error
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return - the {@link HttpServletResponse} writer      * @throws IOException -      *             If an I/O exception occures      */
DECL|method|getWriter
specifier|public
name|Writer
name|getWriter
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|this
operator|.
name|response
operator|.
name|getWriter
argument_list|()
return|;
block|}
comment|/**      * Sends a response for a get e.g. query request. This method must not      * invoked in a case of an error performing the requeste action.      *       * @param feed -      *            the feed to respond to the client      * @param profile -      *            the extension profil for the feed to write      * @throws IOException -      *             if an I/O exception accures, often caused by an already      *             closed Writer or OutputStream      *       */
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|BaseFeed
name|feed
parameter_list|,
name|ExtensionProfile
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|feed
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"feed must not be null"
argument_list|)
throw|;
if|if
condition|(
name|profile
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"extension profil must not be null"
argument_list|)
throw|;
name|DateTime
name|time
init|=
name|feed
operator|.
name|getUpdated
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|!=
literal|null
condition|)
name|setLastModifiedHeader
argument_list|(
name|time
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|XmlWriter
name|writer
init|=
name|createWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|outputFormat
operator|.
name|equals
argument_list|(
name|OutputFormat
operator|.
name|ATOM
argument_list|)
condition|)
block|{
name|this
operator|.
name|response
operator|.
name|setContentType
argument_list|(
name|XMLMIME_ATOM
argument_list|)
expr_stmt|;
name|feed
operator|.
name|generateAtom
argument_list|(
name|writer
argument_list|,
name|profile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|response
operator|.
name|setContentType
argument_list|(
name|XMLMIME_RSS
argument_list|)
expr_stmt|;
name|feed
operator|.
name|generateRss
argument_list|(
name|writer
argument_list|,
name|profile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *       * Sends a response for an update, insert or delete request. This method      * must not invoked in a case of an error performing the requeste action. If      * the specified response format is ATOM the default namespace will be set      * to ATOM.      *       * @param entry -      *            the modified / created entry to send      * @param profile -      *            the entries extension profile      * @throws IOException -      *             if an I/O exception accures, often caused by an already      *             closed Writer or OutputStream      */
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|BaseEntry
name|entry
parameter_list|,
name|ExtensionProfile
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"entry must not be null"
argument_list|)
throw|;
if|if
condition|(
name|profile
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"extension profil must not be null"
argument_list|)
throw|;
name|DateTime
name|time
init|=
name|entry
operator|.
name|getUpdated
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|!=
literal|null
condition|)
name|setLastModifiedHeader
argument_list|(
name|time
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|XmlWriter
name|writer
init|=
name|createWriter
argument_list|()
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|outputFormat
operator|.
name|equals
argument_list|(
name|OutputFormat
operator|.
name|ATOM
argument_list|)
condition|)
name|entry
operator|.
name|generateAtom
argument_list|(
name|writer
argument_list|,
name|profile
argument_list|)
expr_stmt|;
else|else
name|entry
operator|.
name|generateRss
argument_list|(
name|writer
argument_list|,
name|profile
argument_list|)
expr_stmt|;
block|}
DECL|method|createWriter
specifier|private
name|XmlWriter
name|createWriter
parameter_list|()
throws|throws
name|IOException
block|{
name|XmlWriter
name|writer
init|=
operator|new
name|XmlWriter
argument_list|(
name|getWriter
argument_list|()
argument_list|,
name|this
operator|.
name|encoding
argument_list|)
decl_stmt|;
comment|// set the default namespace to Atom if Atom is the response format
if|if
condition|(
name|this
operator|.
name|outputFormat
operator|.
name|equals
argument_list|(
name|OutputFormat
operator|.
name|ATOM
argument_list|)
condition|)
name|writer
operator|.
name|setDefaultNamespace
argument_list|(
name|DEFAULT_NAMESPACE
argument_list|)
expr_stmt|;
return|return
name|writer
return|;
block|}
comment|/**      * This encoding will be used to encode the xml representation of feed or      * entry written to the {@link HttpServletResponse} output stream.      *       * @return - the entry / feed encoding      */
DECL|method|getEncoding
specifier|public
name|String
name|getEncoding
parameter_list|()
block|{
return|return
name|this
operator|.
name|encoding
return|;
block|}
comment|/**      * This encoding will be used to encode the xml representation of feed or      * entry written to the {@link HttpServletResponse} output stream.<i>UTF-8</i>      *<i>ISO-8859-1</i>      *       * @param encoding -      *            string represents the encoding      */
DECL|method|setEncoding
specifier|public
name|void
name|setEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
comment|/**      * @return - the response      *         {@link org.apache.lucene.gdata.server.GDataRequest.OutputFormat}      */
DECL|method|getOutputFormat
specifier|public
name|OutputFormat
name|getOutputFormat
parameter_list|()
block|{
return|return
name|this
operator|.
name|outputFormat
return|;
block|}
comment|/**      * @param outputFormat -      *            the response      *            {@link org.apache.lucene.gdata.server.GDataRequest.OutputFormat}      */
DECL|method|setOutputFormat
specifier|public
name|void
name|setOutputFormat
parameter_list|(
name|OutputFormat
name|outputFormat
parameter_list|)
block|{
name|this
operator|.
name|outputFormat
operator|=
name|outputFormat
expr_stmt|;
block|}
comment|/**      * @see Object#toString()      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|" GDataResponse: "
argument_list|)
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Error: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|error
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" outputFormat: "
argument_list|)
operator|.
name|append
argument_list|(
name|getOutputFormat
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|" encoding: "
argument_list|)
operator|.
name|append
argument_list|(
name|this
operator|.
name|encoding
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|setLastModifiedHeader
specifier|protected
name|void
name|setLastModifiedHeader
parameter_list|(
name|long
name|lastModified
parameter_list|)
block|{
name|String
name|lastMod
init|=
name|DateFormater
operator|.
name|formatDate
argument_list|(
operator|new
name|Date
argument_list|(
name|lastModified
argument_list|)
argument_list|,
name|DateFormater
operator|.
name|HTTP_HEADER_DATE_FORMAT
argument_list|)
decl_stmt|;
name|this
operator|.
name|response
operator|.
name|setHeader
argument_list|(
name|HEADER_LASTMODIFIED
argument_list|,
name|lastMod
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see HttpServletResponse#setStatus(int)      * @param status - the request status code      */
DECL|method|setStatus
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|status
parameter_list|)
block|{
name|this
operator|.
name|response
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

