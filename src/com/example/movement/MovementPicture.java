package com.example.movement;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.R.integer;
import android.opengl.GLES20;

import com.example.forpublicuse.Constant;
import com.example.forpublicuse.MatrixState;
import com.example.forpublicuse.ShaderUtil;
import com.example.opengldemo3.MySurfaceView;

public class MovementPicture {
	int program;//自定义渲染管线着色器程序id
    int maPositionHandle;//获取程序中顶点位置属性引用  
    int maTexCoorHandle;//获取程序中顶点纹理坐标属性引用  
	FloatBuffer mVertexBuffer;// 顶点坐标数据缓冲
	FloatBuffer fTextureBuffer;//纹理数据buffer
	int vCount;//顶点的个数
	int muMVPMatrixHandle;//获取程序中总变换矩阵引用
	String mVertexShader;// 顶点着色器
	String mFragmentShader;// 片元着色器
	float screenHeight,screenWidth;
    int fuRatioHandle;//三角形的缩放比例id

	public MovementPicture(int program,float screenHeight,float screenWidth) {
		// 初始化顶点坐标与着色数据
		initVertexData(screenHeight,screenWidth);
		// 初始化shader
		this.program=program;
		initShader();
	}

	// 初始化顶点坐标数据的方法
	public void initVertexData(float height,float wide) {
		// 顶点坐标数据的初始化================begin============================
		ArrayList<Float> al_vertex=new ArrayList<Float>();//存储顶点信息
		ArrayList<Float> al_texture=new ArrayList<Float>();//存储纹理信息
		int levelh=160,levelw=90;
		float everyheight=height/levelh;
		float everywide=wide/levelw;
		for(int i=levelh/2;i>-levelh/2;i--)//每层进行扫描
		{		//当前层顶端最左边点的坐标
			float topEdgeFirstPointX=-(levelw/2)*everywide;
			float topEdgeFirstPointY=i*everyheight;
			float topEdgeFirstPointZ=0;
			
			//当前层底端最左边点的坐标
			float bottomEdgeFirstPointX=-(levelw/2)*everywide+everywide;
			float bottomEdgeFirstPointY=(i-1)*everyheight;
			float bottomEdgeFirstPointZ=0;
			//---------------纹理----------------
			float hSpan=1/(float)levelh;//横向纹理的偏移量
			float wSpan=1/(float)levelw;//纵向纹理的偏移量
			//当前层顶端第一个纹理坐标相关参数问题
			float topFirstS=0;
			float topFirstT=(levelh/2-i)*hSpan;
			//当前层底端第一个纹理坐标的相关参数
			float bottomFirstS=wSpan;
			float bottomFirstT=(levelh/2-i+1)*hSpan;
			for(int j=0;j<levelw;j++){
				//顶点
				float topX=topEdgeFirstPointX+j*everywide;
				float topY=topEdgeFirstPointY;
				float topZ=topEdgeFirstPointZ;
				float topS=topFirstS+j*wSpan;
				float topT=topFirstT;
				//左下点
				float leftBottomX=bottomEdgeFirstPointX+j*everywide;
				float leftBottomY=bottomEdgeFirstPointY;
				float leftBottomZ=bottomEdgeFirstPointZ;
				float leftBottomS=bottomFirstS+j*wSpan;
				float leftBottomT=bottomFirstT;
				//右下点
				float rightX=topX+everywide;
				float rightY=topY;
				float rightZ=topZ;
				float rightS=topS+wSpan;
				float rightT=topT;
				//逆时针卷绕----- 纹理绘制方式
				al_vertex.add(topX);al_vertex.add(topY);al_vertex.add(topZ);
				al_vertex.add(leftBottomX);al_vertex.add(leftBottomY);al_vertex.add(leftBottomZ);
				al_vertex.add(rightX);al_vertex.add(rightY);al_vertex.add(rightZ);
				//-------纹理绘制方式
				al_texture.add(topS);al_texture.add(topT);
				al_texture.add(leftBottomS);al_texture.add(leftBottomT);
				al_texture.add(rightS);al_texture.add(rightT);
				
			}
			for(int j=0;j<levelw;j++){
				//左上点
				float TopX=topEdgeFirstPointX+j*everywide;
				float TopY=topEdgeFirstPointY;
				float TopZ=topEdgeFirstPointZ;
				float TopS=topFirstS+j*wSpan;
				float TopT=topFirstT;
				//底端点
				float leftX=TopX;
				float leftY=TopY-everyheight;
				float leftZ=TopZ;
				float leftS=TopS;
				float leftT=TopT+hSpan;
				//右端点
				float rightX=bottomEdgeFirstPointX+j*everywide;
				float rightY=bottomEdgeFirstPointY;
				float rightZ=bottomEdgeFirstPointZ;
				float rightS=bottomFirstS+j*wSpan;
				float rightT=bottomFirstT;
				//逆时针卷绕-----
				al_vertex.add(TopX);al_vertex.add(TopY);al_vertex.add(TopZ);
				al_vertex.add(leftX);al_vertex.add(leftY);al_vertex.add(leftZ);
				al_vertex.add(rightX);al_vertex.add(rightY);al_vertex.add(rightZ);
				
				al_texture.add(TopS);al_texture.add(TopT);
				al_texture.add(leftS);al_texture.add(leftT);
				al_texture.add(rightS);al_texture.add(rightT);
			}
			
		}
		//加载进顶点缓冲
		int vertexSize=al_vertex.size();
		vCount=vertexSize/3;//确定顶点的个数	
		float vertexs[]=new float[vertexSize];
		for(int i=0;i<vertexSize;i++)
		{
			vertexs[i]=al_vertex.get(i);
		}
		// 创建顶点坐标数据缓冲
		// vertices.length*4是因为一个整数四个字节
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexSize * 4);
		vbb.order(ByteOrder.nativeOrder());// 设置字节顺序
		mVertexBuffer = vbb.asFloatBuffer();// 转换为int型缓冲
		mVertexBuffer.put(vertexs);// 向缓冲区中放入顶点坐标数据
		mVertexBuffer.position(0);// 设置缓冲区起始位置
		// 特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
		// 转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
		al_vertex=null;

		//加载进纹理缓冲
		int textureSize=al_texture.size();
		float textures[]=new float[textureSize];
		for(int i=0;i<textureSize;i++)
		{
			textures[i]=al_texture.get(i);
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(textureSize*4);
		tbb.order(ByteOrder.nativeOrder());
		fTextureBuffer=tbb.asFloatBuffer();
		fTextureBuffer.put(textures);
		fTextureBuffer.position(0);
		al_texture=null;
	}

	// 初始化shader
	public void initShader() {

		//获取程序中顶点位置属性引用  
        maPositionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        //获取程序中顶点纹理坐标属性引用  
        maTexCoorHandle= GLES20.glGetAttribLocation(program, "aTexCoor");
        //获取程序中总变换矩阵引用
        muMVPMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        //缩放比例
        fuRatioHandle = GLES20.glGetUniformLocation(program, "ratio");
	}

	public void drawSelf(int texId,float twistingRatio) {
		//制定使用某套shader程序
   	 	GLES20.glUseProgram(program); 
        //将最终变换矩阵传入shader程序
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0); 
        //将缩放比例传入shader程序
        GLES20.glUniform1f(fuRatioHandle, twistingRatio); 
        //将顶点位置数据传入渲染管线
        GLES20.glVertexAttribPointer
		(
			maPositionHandle, 
			3, 
			GLES20.GL_FLOAT, 
			false, 
			3*4, 
			mVertexBuffer
		);
		//将纹理坐标数据传入渲染管线
		GLES20.glVertexAttribPointer
		(
			maTexCoorHandle, 
			2, 
			GLES20.GL_FLOAT, 
			false, 
			2*4, 
			fTextureBuffer
		);
		//启用顶点位置数据
        GLES20.glEnableVertexAttribArray(maPositionHandle);  
        GLES20.glEnableVertexAttribArray(maTexCoorHandle);  
        //绑定纹理
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
        //绘制纹理矩形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);

	}
}
