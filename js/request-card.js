export async function downloadRequestCard(request, { includeContact = false } = {}) {
  if (document.fonts?.ready) await document.fonts.ready;
  const canvas = document.createElement('canvas');
  canvas.width = 1080;
  canvas.height = 1350;
  const context = canvas.getContext('2d');
  if (!context) throw new Error('Canvas is unavailable');
  drawRequestCard(context, canvas.width, canvas.height, request, includeContact);
  const image = await new Promise((resolve, reject) => canvas.toBlob((blob) => blob ? resolve(blob) : reject(new Error('Could not create image')), 'image/png'));
  const url = URL.createObjectURL(image);
  const link = document.createElement('a');
  const bloodType = String(request.bloodType || 'blood').replace(/[^a-z0-9]/gi, '');
  link.href = url;
  link.download = `lifelink-blood-request-${bloodType}.png`;
  link.click();
  URL.revokeObjectURL(url);
}

function drawRequestCard(context, width, height, request, includeContact) {
  const background = context.createLinearGradient(0, 0, width, height);
  background.addColorStop(0, '#fff8f3');
  background.addColorStop(1, '#f3ded7');
  context.fillStyle = background;
  context.fillRect(0, 0, width, height);

  context.fillStyle = '#bd3f36';
  roundedRect(context, 54, 54, width - 108, height - 108, 42);
  context.fill();
  context.fillStyle = 'rgba(255,255,255,.07)';
  context.beginPath();
  context.arc(width - 80, 88, 245, 0, Math.PI * 2);
  context.fill();
  context.beginPath();
  context.arc(70, height - 90, 195, 0, Math.PI * 2);
  context.fill();

  context.fillStyle = '#ffe1d8';
  context.font = '600 28px Poppins, Arial, sans-serif';
  context.fillText('LIFELINK  ·  BMU STUDENTS PLATFORM', 112, 150);

  context.fillStyle = '#ffffff';
  context.font = '700 67px Poppins, Arial, sans-serif';
  context.fillText('BLOOD NEEDED', 112, 285);

  context.fillStyle = '#fff';
  context.beginPath();
  context.arc(width - 245, 420, 112, 0, Math.PI * 2);
  context.fill();
  context.fillStyle = '#bd3f36';
  context.textAlign = 'center';
  context.font = '800 76px Poppins, Arial, sans-serif';
  context.fillText(request.bloodType || 'Any', width - 245, 447);
  context.textAlign = 'left';

  context.fillStyle = '#f8dcd4';
  context.font = '600 27px Poppins, Arial, sans-serif';
  context.fillText('UNITS NEEDED', 112, 390);
  context.fillStyle = '#fff';
  context.font = '700 43px Poppins, Arial, sans-serif';
  const units = String(request.units || '1');
  context.fillText(units === '4' ? '4+ units' : `${units} ${units === '1' ? 'unit' : 'units'}`, 112, 448);

  context.fillStyle = '#f8dcd4';
  context.font = '600 25px Poppins, Arial, sans-serif';
  context.fillText('WHEN', 112, 575);
  context.fillStyle = '#fff';
  context.font = '600 37px Poppins, Arial, sans-serif';
  writeWrappedText(context, request.urgency || 'Contact coordinator', 112, 630, 850, 48, 2);

  context.fillStyle = '#f8dcd4';
  context.font = '600 25px Poppins, Arial, sans-serif';
  context.fillText('HOSPITAL / LOCATION', 112, 770);
  context.fillStyle = '#fff';
  context.font = '600 35px Poppins, Arial, sans-serif';
  writeWrappedText(context, request.hospital || 'Location not specified', 112, 825, 850, 45, 2);

  if (includeContact && request.phone) {
    context.fillStyle = '#f8dcd4';
    context.font = '600 25px Poppins, Arial, sans-serif';
    context.fillText('CONTACT', 112, 980);
    context.fillStyle = '#fff';
    context.font = '600 32px Poppins, Arial, sans-serif';
    writeWrappedText(context, `${request.name || ''} · ${request.phone}`, 112, 1035, 850, 43, 2);
  }

  context.fillStyle = '#f8dcd4';
  context.font = '500 22px Poppins, Arial, sans-serif';
  context.fillText('Please share to help reach a compatible donor.', 112, 1154);
  context.fillStyle = '#ffe1d8';
  context.font = '500 19px Poppins, Arial, sans-serif';
  context.fillText('Community coordination only · Contact healthcare services for emergencies', 112, 1203);
}

function writeWrappedText(context, text, x, y, maxWidth, lineHeight, maxLines) {
  const words = String(text).split(/\s+/);
  let line = '';
  let lineNumber = 0;
  for (const word of words) {
    const nextLine = line ? `${line} ${word}` : word;
    if (context.measureText(nextLine).width > maxWidth && line) {
      context.fillText(line, x, y + lineNumber * lineHeight);
      line = word;
      lineNumber += 1;
      if (lineNumber >= maxLines) break;
    } else {
      line = nextLine;
    }
  }
  if (lineNumber < maxLines && line) context.fillText(line, x, y + lineNumber * lineHeight);
}

function roundedRect(context, x, y, width, height, radius) {
  context.beginPath();
  context.roundRect(x, y, width, height, radius);
}
