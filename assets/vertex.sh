uniform mat4 uMVPMatrix; //总变换矩阵
uniform mat4 uMMatrix; //变换矩阵
uniform vec3 uCamera;	//摄像机位置
attribute vec3 aPosition;  //顶点位置
attribute vec4 aColor;    //顶点颜色
varying  vec4 aaColor;  //用于传递给片元着色器的变量
varying vec3 vPosition;//用于传递给片元着色器的顶点位置
void main()     
{                   
   //根据总变换矩阵计算此次绘制此顶点位置         		
   gl_Position = uMVPMatrix * vec4(aPosition,1); 

   aaColor = aColor;//将接收的颜色传递给片元着色器
   
}                      