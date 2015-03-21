precision mediump float;
uniform float uR;
varying vec4 aaColor; //接收从顶点着色器过来的参数
void main()                         
{

   vec4 finalColor=aaColor;
   //给此片元颜色值
   gl_FragColor=finalColor;
}     