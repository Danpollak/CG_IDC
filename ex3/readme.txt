The trackball implementation broke the alg' into 3 parts.
We first switch to a contained plane coordinations and project the points of the event.
The second part consists of finding the rotation axis, rotation matrix and performing the rotation (while remembering the rotation matrix) by multiplying by the rotation matrix.
The third part takes care of the zoom.
It is worth to note that the algebraic properties of matrix multiplication enable us to break the process to separate parts, and easily combining them while utilizing openGL's optimized vector algebra abilities

The Locomotive model is separated to an engine and cabin.
The engine and the cabin are the big parts that consist of smaller parts.
before drawing we push a new matrix and after drawing we pop it to create the more symmetrical parts of the model.
Most parts are created by quad vertices, with the more circular parts using GLU.
