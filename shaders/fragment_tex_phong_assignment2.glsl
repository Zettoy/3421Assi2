
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightDir;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

uniform int nightMode;
uniform vec3 torchlightPos;
uniform vec3 torchlightIntensity;
uniform float torchlightAngle;
uniform float attenuation;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

vec4 sunlight()
{
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightDir,0)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    return ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}

vec4 torchlight()
{
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(torchlightPos,1) - viewPosition).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));

    float distance = length(view_matrix*vec4(torchlightPos,1) - viewPosition);
    float atte = 1.0 / (1.0 + attenuation * pow(distance, 2));

    float angle = degrees(acos(dot(s, v)));
    if(angle > torchlightAngle) atte = 0.0;

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(torchlightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(torchlightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    vec4 ambientAndDiffuse = vec4(ambient + atte * (diffuse + specular), 1);

    return ambientAndDiffuse*input_color*texture(tex, texCoordFrag);
}

void main()
{
    outputColor = sunlight();
    if (nightMode == 1) outputColor += torchlight();
}